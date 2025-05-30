package com.focussu.backend.member.controller;

import com.focussu.backend.auth.util.AuthUtil;
import com.focussu.backend.common.dto.ErrorResponse;
import com.focussu.backend.member.dto.MemberCreateRequest;
import com.focussu.backend.member.dto.MemberCreateResponse;
import com.focussu.backend.member.service.MemberCommandService;
import com.focussu.backend.member.service.MemberQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Member", description = "회원가입, 조회, 탈퇴 관련 API")
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService commandService;
    private final MemberQueryService queryService;
    private final AuthUtil authUtil;

    @Operation(summary = "회원가입", description = "신규 회원을 등록합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = MemberCreateResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 존재하는 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "중복 이메일 예시",
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "이미 존재하는 이메일입니다.",
                                              "code": "MEMBER_ALREADY_EXISTS",
                                              "isSuccess": false
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/join")
    public ResponseEntity<MemberCreateResponse> createMember(
            @RequestBody @Valid MemberCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commandService.createMember(request));
    }

    @Operation(summary = "회원 조회", description = "회원 ID로 회원 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = MemberCreateResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원이 존재하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "회원 없음 예시",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "회원을 찾을 수 없습니다.",
                                              "code": "MEMBER_NOT_FOUND",
                                              "isSuccess": false
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberCreateResponse> getMember(
            @Parameter(description = "회원 ID", example = "1")
            @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(queryService.getMember(memberId));
    }

    @Operation(summary = "회원 탈퇴", description = "회원 ID로 회원을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공 (내용 없음)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원이 존재하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "회원 없음 예시",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "회원을 찾을 수 없습니다.",
                                              "code": "MEMBER_NOT_FOUND",
                                              "isSuccess": false
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "회원 ID", example = "1")
            @PathVariable Long memberId
    ) {
        commandService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 정보 조회", description = "내 토큰으로 내 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = MemberCreateResponse.class)
                    )
            )
    })
    @GetMapping("/my")
    public ResponseEntity<MemberCreateResponse> getMyInformation() {
        Long currentMemberId = authUtil.getCurrentMemberId();
        return ResponseEntity.ok(queryService.getMember(currentMemberId));
    }

}
