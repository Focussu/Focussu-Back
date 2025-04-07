package com.focussu.backend.member.service;

import com.focussu.backend.member.dto.MemberResponse;
import com.focussu.backend.member.model.Member;
import com.focussu.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return MemberResponse.from(member);
    }
}
