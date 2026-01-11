package com.ocare.domain.member.controller;

import com.ocare.common.util.ResponseUtil;
import com.ocare.domain.member.dto.request.LoginRequest;
import com.ocare.domain.member.dto.request.SignUpRequest;
import com.ocare.domain.member.dto.response.LoginResponse;
import com.ocare.domain.member.dto.response.MemberResponse;
import com.ocare.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<MemberResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseUtil.created(memberService.signUp(request));
    }

    /**
     * 로그인
     * POST /api/members/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseUtil.ok(memberService.login(request));
    }
}
