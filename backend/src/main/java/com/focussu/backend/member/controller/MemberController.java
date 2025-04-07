package com.focussu.backend.member.controller;

import com.focussu.backend.member.dto.MemberRequest;
import com.focussu.backend.member.dto.MemberResponse;
import com.focussu.backend.member.service.MemberCommandService;
import com.focussu.backend.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService commandService;
    private final MemberQueryService queryService;

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest request) {
        return ResponseEntity.ok(commandService.createMember(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getMember(id));
    }
}
