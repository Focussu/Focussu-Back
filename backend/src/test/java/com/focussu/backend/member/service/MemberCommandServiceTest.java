package com.focussu.backend.member.service;

import com.focussu.backend.member.dto.MemberCreateRequest;
import com.focussu.backend.member.dto.MemberCreateResponse;
import com.focussu.backend.member.exception.MemberException;
import com.focussu.backend.member.model.Member;
import com.focussu.backend.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberCommandServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberCommandService memberCommandService;

    /**
     * createMember의 정상 동작 테스트: 사용되는 name, email 값을 "oxdjww", "oxdjww@example.com"으로 설정
     */
    @Test
    public void testCreateMemberSuccess() {
        // Given
        String name = "oxdjww";
        String email = "oxdjww@example.com";
        String rawPassword = "securePass";
        String encodedPassword = "encodedSecurePass";
        Long memberId = 1L;

        MemberCreateRequest request = new MemberCreateRequest(name, email, rawPassword);

        // 동일 이메일이 없음을 가정
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());
        // 비밀번호 암호화 모킹
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        Member member = Member.builder()
                .id(memberId)
                .name(name)
                .email(email)
                .password(encodedPassword)
                .build();
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // When
        MemberCreateResponse response = memberCommandService.createMember(request);

        // Then
        assertNotNull(response, "응답은 null이 아니어야 합니다.");
        assertEquals(memberId, response.id(), "회원 ID가 일치해야 합니다.");
        assertEquals(name, response.name(), "회원 이름이 일치해야 합니다.");
        assertEquals(email, response.email(), "회원 이메일이 일치해야 합니다.");

        verify(memberRepository, times(1)).save(any(Member.class));
    }

    /**
     * 중복 이메일로 인한 회원가입 실패 테스트: 테스트 입력 값도 "oxdjww", "oxdjww@example.com"으로 설정
     */
    @Test
    public void testCreateMemberDuplicateEmail() {
        // Given
        String name = "oxdjww";
        String email = "oxdjww@example.com";
        String rawPassword = "anotherPass";

        MemberCreateRequest request = new MemberCreateRequest(name, email, rawPassword);

        // 이미 동일 이메일로 등록된 회원이 존재하는 상황
        Member existingMember = Member.builder()
                .id(2L)
                .name(name)
                .email(email)
                .password("encodedPass")
                .build();
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(existingMember));

        // When & Then: 중복 이메일이면 RuntimeException 발생 확인
        MemberException exception = assertThrows(MemberException.class, () -> {
            memberCommandService.createMember(request);
        });
        assertEquals("이미 등록된 이메일입니다.", exception.getMessage(), "예외 메시지가 일치해야 합니다.");
    }

    /**
     * loadUserByUsername의 정상 동작 테스트
     */
    @Test
    public void testLoadUserByUsernameSuccess() {
        // Given
        String email = "oxdjww@example.com";
        String encodedPassword = "encodedPass";

        Member member = Member.builder()
                .id(1L)
                .name("oxdjww")
                .email(email)
                .password(encodedPassword)
                .build();
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // When
        var userDetails = memberCommandService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails, "UserDetails는 null이 아니어야 합니다.");
        assertEquals(email, userDetails.getUsername(), "이메일이 일치해야 합니다.");
        assertEquals(encodedPassword, userDetails.getPassword(), "암호화된 비밀번호가 일치해야 합니다.");
    }

    /**
     * loadUserByUsername 실패 테스트 (존재하지 않는 이메일)
     */
    @Test
    public void testLoadUserByUsernameNotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then: 이메일이 존재하지 않으면 UsernameNotFoundException 발생해야 함
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            memberCommandService.loadUserByUsername(email);
        });
        assertTrue(exception.getMessage().contains("User not found with email: " + email),
                "예외 메시지에 이메일 정보가 포함되어야 합니다.");
    }

    /**
     * 소프트 딜리트를 사용하는 deleteMember 테스트: 테스트 입력 값은 "oxdjww", "oxdjww@example.com" 사용
     */
    @Test
    public void testDeleteMemberSoftDelete() {
        // Given
        Long memberId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .name("oxdjww")
                .email("oxdjww@example.com")
                .password("encodedPass")
                .build();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        memberCommandService.deleteMember(memberId);

        // Then: 소프트 딜리트가 적용되어 isDeleted가 true가 되었는지 검증
        assertTrue(member.getIsDeleted(), "회원은 소프트 딜리트되어야 하므로 isDeleted는 true여야 합니다.");
        verify(memberRepository, times(1)).save(member);
    }
}
