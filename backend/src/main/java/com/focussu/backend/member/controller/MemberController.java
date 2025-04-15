package com.focussu.backend.member.controller;

import com.focussu.backend.member.dto.MemberCreateRequest;
import com.focussu.backend.member.dto.MemberCreateResponse;
import com.focussu.backend.member.service.MemberCommandService;
import com.focussu.backend.member.service.MemberQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Member", description = "회원가입, 조회, 탈퇴 관련 API")
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService commandService;
    private final MemberQueryService queryService;

    @Operation(summary = "회원가입", description = "신규 회원을 등록합니다.")
    @PostMapping
    public ResponseEntity<MemberCreateResponse> createMember(@RequestBody MemberCreateRequest request) {
        return ResponseEntity.ok(commandService.createMember(request));
    }

    @Operation(summary = "회원 조회", description = "회원 ID로 회원 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<MemberCreateResponse> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getMember(id));
    }

    @Operation(summary = "회원 탈퇴", description = "회원 ID로 회원을 삭제합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        commandService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
