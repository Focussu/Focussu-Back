package com.focussu.backend.auth.util;

import com.focussu.backend.member.exception.MemberException;
import com.focussu.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.focussu.backend.common.exception.ErrorCode.STUDYROOM_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    private final MemberRepository memberRepository;

    public Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(STUDYROOM_NOT_FOUND))
                .getId();
    }
}
