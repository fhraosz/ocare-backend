package com.ocare.domain.health.service;

import com.ocare.common.util.DateTimeUtil;
import com.ocare.domain.health.dto.request.EntryDto;
import com.ocare.domain.health.dto.request.HealthDataRequest;
import com.ocare.domain.health.dto.response.HealthDataSaveResponse;
import com.ocare.domain.health.entity.HealthEntryEntity;
import com.ocare.domain.health.repository.HealthEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HealthDataService {

    private final HealthEntryRepository healthEntryRepository;
    private final HealthAggregationService aggregationService;

    /**
     * 건강 데이터 저장 및 집계 처리
     */
    public HealthDataSaveResponse saveHealthData(HealthDataRequest request) {
        String recordKey = request.getRecordKey();
        List<EntryDto> entries = request.getData().getEntries();

        if (entries == null || entries.isEmpty()) {
            log.warn("Empty entries for recordKey: {}", recordKey);
            return HealthDataSaveResponse.of(recordKey, 0);
        }

        log.debug("건강 데이터 저장 시작: recordKey={}, entryCount={}", recordKey, entries.size());

        List<HealthEntryEntity> entriesToSave = new ArrayList<>();
        int savedCount = processEntries(recordKey, entries, entriesToSave);

        healthEntryRepository.saveAll(entriesToSave);
        log.info("건강 데이터 저장 완료: recordKey={}, savedCount={}", recordKey, savedCount);

        aggregationService.updateAggregations(recordKey);

        return HealthDataSaveResponse.of(recordKey, savedCount);
    }

    /**
     * 건강 데이터 엔트리 목록 처리
     */
    private int processEntries(String recordKey, List<EntryDto> entries, List<HealthEntryEntity> entriesToSave) {
        int savedCount = 0;

        for (EntryDto entry : entries) {
            try {
                HealthEntryEntity healthEntry = parseAndSaveEntry(recordKey, entry);
                entriesToSave.add(healthEntry);
                savedCount++;
            } catch (Exception e) {
                log.error("Failed to parse entry: {}", e.getMessage());
            }
        }

        return savedCount;
    }

    /**
     * 단일 엔트리 파싱 및 엔티티 생성 (기존 데이터 있으면 업데이트)
     */
    private HealthEntryEntity parseAndSaveEntry(String recordKey, EntryDto entry) {
        LocalDateTime periodFrom = DateTimeUtil.parse(entry.getPeriod().getFrom());
        LocalDateTime periodTo = DateTimeUtil.parse(entry.getPeriod().getTo());
        Integer steps = entry.getStepsAsInteger();
        Float calories = entry.getCalories().getValueAsFloat();
        Float distance = entry.getDistance().getValueAsFloat();

        Optional<HealthEntryEntity> existingEntry = healthEntryRepository
                .findByRecordKeyAndPeriodFromAndPeriodTo(recordKey, periodFrom, periodTo);

        if (existingEntry.isPresent()) {
            existingEntry.get().update(steps, calories, distance);
            return existingEntry.get();
        }

        return HealthEntryEntity.of(recordKey, periodFrom, periodTo, steps, calories, distance);
    }
}
