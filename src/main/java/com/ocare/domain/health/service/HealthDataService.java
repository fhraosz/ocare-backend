package com.ocare.domain.health.service;

import com.ocare.domain.health.dto.HealthDataRequest;
import com.ocare.domain.health.entity.HealthEntryEntity;
import com.ocare.domain.health.repository.HealthEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 건강 데이터 저장 서비스
 * JSON 데이터 파싱 및 저장 로직
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HealthDataService {

    private final HealthEntryRepository healthEntryRepository;
    private final HealthAggregationService aggregationService;

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+'SSSS"),
            DateTimeFormatter.ISO_DATE_TIME
    };

    /**
     * JSON 데이터 저장
     */
    public int saveHealthData(HealthDataRequest request) {
        String recordKey = request.getRecordKey();
        List<HealthDataRequest.EntryDto> entries = request.getData().getEntries();

        if (entries == null || entries.isEmpty()) {
            log.warn("Empty entries for recordKey: {}", recordKey);
            return 0;
        }

        log.debug("건강 데이터 저장 시작: recordKey={}, entryCount={}", recordKey, entries.size());

        int savedCount = 0;
        List<HealthEntryEntity> entriesToSave = new ArrayList<>();

        for (HealthDataRequest.EntryDto entry : entries) {
            try {
                LocalDateTime periodFrom = parseDateTime(entry.getPeriod().getFrom());
                LocalDateTime periodTo = parseDateTime(entry.getPeriod().getTo());
                Integer steps = entry.getStepsAsInteger();
                Float calories = entry.getCalories().getValueAsFloat();
                Float distance = entry.getDistance().getValueAsFloat();

                Optional<HealthEntryEntity> existingEntry = healthEntryRepository
                        .findByRecordKeyAndPeriodFromAndPeriodTo(recordKey, periodFrom, periodTo);

                if (existingEntry.isPresent()) {
                    existingEntry.get().update(steps, calories, distance);
                    entriesToSave.add(existingEntry.get());
                } else {
                    HealthEntryEntity healthEntry = HealthEntryEntity.of(
                            recordKey, periodFrom, periodTo, steps, calories, distance);
                    entriesToSave.add(healthEntry);
                }
                savedCount++;

            } catch (Exception e) {
                log.error("Failed to parse entry: {}", e.getMessage());
            }
        }

        healthEntryRepository.saveAll(entriesToSave);
        log.info("건강 데이터 저장 완료: recordKey={}, savedCount={}", recordKey, savedCount);

        aggregationService.updateAggregations(recordKey);

        return savedCount;
    }

    /**
     * 여러 형식의 날짜 문자열 파싱
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            throw new IllegalArgumentException("DateTime string is empty");
        }

        if (dateTimeStr.contains("+") && !dateTimeStr.contains("T")) {
            dateTimeStr = dateTimeStr.replace(" ", "T");
        }

        if (dateTimeStr.matches(".*\\+\\d{4}$")) {
            dateTimeStr = dateTimeStr.substring(0, dateTimeStr.length() - 2) + ":" +
                    dateTimeStr.substring(dateTimeStr.length() - 2);
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateTimeStr, formatter);
            } catch (DateTimeParseException e) {
                // 다음 형식 시도
            }
        }

        try {
            return LocalDateTime.parse(dateTimeStr.substring(0, 19),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } catch (Exception e) {
            // ignore
        }

        try {
            return LocalDateTime.parse(dateTimeStr.replace("T", " ").substring(0, 19),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse datetime: " + dateTimeStr);
        }
    }
}
