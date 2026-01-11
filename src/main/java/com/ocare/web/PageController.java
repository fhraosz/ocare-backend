package com.ocare.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 페이지 렌더링용 컨트롤러
 * Thymeleaf 템플릿을 반환
 */
@Controller
public class PageController {

    /**
     * 메인 페이지 - 로그인 페이지로 리다이렉트
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }

    /**
     * 대시보드 페이지
     * JWT 인증은 JavaScript에서 처리
     */
    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard/index";
    }
}
