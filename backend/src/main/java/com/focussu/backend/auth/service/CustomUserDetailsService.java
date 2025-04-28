package com.focussu.backend.auth.service;

import com.focussu.backend.auth.exception.AuthException;
import com.focussu.backend.common.exception.ErrorCode;
import com.focussu.backend.member.model.Member;
import com.focussu.backend.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Component
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(ErrorCode.AUTH_INVALID_CREDENTIALS));
        if (member.getIsDeleted()) {
            throw new UsernameNotFoundException("계정이 삭제되었습니다: " + email);
        }
        return new User(member.getEmail(), member.getPassword(), new ArrayList<>());
    }

}
