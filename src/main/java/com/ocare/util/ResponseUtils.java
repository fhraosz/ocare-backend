package com.ocare.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * HTTP 응답 유틸리티
 * 일관된 응답 형식 제공
 */
@UtilityClass
public class ResponseUtils {

    public static <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    public static <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }

    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static <T> ResponseEntity<T> badRequest(T body) {
        return ResponseEntity.badRequest().body(body);
    }
}
