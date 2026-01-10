package com.ocare.domain.member.controller;

import com.ocare.common.response.ApiResponse;
import com.ocare.domain.member.dto.LoginRequest;
import com.ocare.domain.member.dto.LoginResponse;
import com.ocare.domain.member.dto.MemberResponse;
import com.ocare.domain.member.dto.SignUpRequest;
import com.ocare.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 회원 API 컨트롤러
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     * POST /api/members/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(
            @Valid @RequestBody SignUpRequest request) {
        MemberResponse response = memberService.signUp(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다", response));
    }

    /**
     * 로그인
     * POST /api/members/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = memberService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }
}
