package com.ocare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 오케어 백엔드 메인 애플리케이션
 * 건강 활동 데이터(삼성헬스/애플건강) 수집 및 저장 서비스
 */
@SpringBootApplication
public class OcareApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcareApplication.class, args);
    }
}
