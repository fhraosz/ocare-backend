package com.ocare.common.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 * ResponseEntity 생성 유틸리티
 */
public final class ResponseUtil {

    private ResponseUtil() {}

    public static <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }

    public static <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    public static <T> ResponseEntity<T> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static <T> ResponseEntity<T> badRequest(T body) {
        return ResponseEntity.badRequest().body(body);
    }

    public static <T> ResponseEntity<T> notFound() {
        return ResponseEntity.notFound().build();
    }

    /**
     * Optional 값이 있으면 200 OK, 없으면 404 Not Found 반환
     */
    public static <T> ResponseEntity<T> okOrNotFound(Optional<T> optional) {
        return optional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
