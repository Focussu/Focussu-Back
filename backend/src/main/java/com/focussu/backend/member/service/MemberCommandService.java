package com.focussu.backend.member.service;

import com.focussu.backend.member.dto.MemberCreateRequest;
import com.focussu.backend.member.dto.MemberCreateResponse;
import com.focussu.backend.member.model.Member;
import com.focussu.backend.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberCreateResponse createMember(MemberCreateRequest request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }
        Member saved = memberRepository.save(
                request.toEntity(passwordEncoder.encode(request.password()))
        );
        return MemberCreateResponse.from(saved);
    }

    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        member.setIsDeleted(true);
        memberRepository.save(member);
    }


    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new org.springframework.security.core.userdetails.User(member.getEmail(), member.getPassword(), new ArrayList<>());
    }
}
