package com.focussu.backend.member.service;

import com.focussu.backend.member.dto.MemberRequest;
import com.focussu.backend.member.dto.MemberResponse;
import com.focussu.backend.member.model.Member;
import com.focussu.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;

    public MemberResponse createMember(MemberRequest request) {
        Member member = memberRepository.save(request.toEntity());
        return MemberResponse.from(member);
    }
}
