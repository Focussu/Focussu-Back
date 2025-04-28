package com.focussu.backend.member.service;

import com.focussu.backend.member.dto.MemberCreateRequest;
import com.focussu.backend.member.dto.MemberCreateResponse;
import com.focussu.backend.member.exception.MemberException;
import com.focussu.backend.member.model.Member;
import com.focussu.backend.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.focussu.backend.common.exception.ErrorCode.MEMBER_ALREADY_EXISTS;
import static com.focussu.backend.common.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberCreateResponse createMember(MemberCreateRequest request) {
        memberRepository.findByEmail(request.email())
                .ifPresent(member -> {
                    throw new MemberException(MEMBER_ALREADY_EXISTS);
                });

        Member saved = memberRepository.save(
                request.toEntity(passwordEncoder.encode(request.password()))
        );
        return MemberCreateResponse.from(saved);
    }


    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        member.setIsDeleted(true);
        memberRepository.save(member);
    }


    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new org.springframework.security.core.userdetails.User(member.getEmail(), member.getPassword(), new ArrayList<>());
    }
}
