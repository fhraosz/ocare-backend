package com.ocare.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+'SSSS"),
            DateTimeFormatter.ISO_DATE_TIME
    };

    private DateTimeUtil() {
    }

    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            throw new IllegalArgumentException("DateTime string is empty");
        }

        String normalized = normalize(dateTimeStr);

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(normalized, formatter);
            } catch (DateTimeParseException e) {
                // 다음 형식 시도
            }
        }

        try {
            return LocalDateTime.parse(normalized.substring(0, 19),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } catch (Exception e) {
            // ignore
        }

        try {
            return LocalDateTime.parse(normalized.replace("T", " ").substring(0, 19),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse datetime: " + dateTimeStr);
        }
    }

    private static String normalize(String dateTimeStr) {
        String result = dateTimeStr;

        if (result.contains("+") && !result.contains("T")) {
            result = result.replace(" ", "T");
        }

        if (result.matches(".*\\+\\d{4}$")) {
            result = result.substring(0, result.length() - 2) + ":" +
                    result.substring(result.length() - 2);
        }

        return result;
    }
}
