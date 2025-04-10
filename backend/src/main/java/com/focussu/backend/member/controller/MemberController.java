package com.focussu.backend.member.controller;

import com.focussu.backend.member.dto.MemberCreateRequest;
import com.focussu.backend.member.dto.MemberCreateResponse;
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
    public ResponseEntity<MemberCreateResponse> createMember(@RequestBody MemberCreateRequest request) {
        return ResponseEntity.ok(commandService.createMember(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberCreateResponse> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getMember(id));
    }
}
