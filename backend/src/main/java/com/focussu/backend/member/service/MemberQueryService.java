package com.focussu.backend.member.service;

import com.focussu.backend.member.dto.MemberCreateResponse;
import com.focussu.backend.member.exception.MemberException;
import com.focussu.backend.member.model.Member;
import com.focussu.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.focussu.backend.common.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public MemberCreateResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        return MemberCreateResponse.from(member);
    }

    public Long getMemberIdByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        return member.getId();
    }
}
