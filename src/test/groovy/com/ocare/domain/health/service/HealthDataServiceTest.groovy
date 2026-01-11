package com.ocare.domain.health.service

import com.ocare.domain.health.dto.request.DataWrapper
import com.ocare.domain.health.dto.request.EntryDto
import com.ocare.domain.health.dto.request.HealthDataRequest
import com.ocare.domain.health.dto.request.PeriodDto
import com.ocare.domain.health.dto.request.ValueDto
import com.ocare.domain.health.dto.response.HealthDataSaveResponse
import com.ocare.domain.health.entity.HealthEntryEntity
import com.ocare.domain.health.repository.HealthEntryRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class HealthDataServiceTest extends Specification {

    HealthEntryRepository healthEntryRepository = Mock()
    HealthAggregationService aggregationService = Mock()

    @Subject
    HealthDataService healthDataService = new HealthDataService(healthEntryRepository, aggregationService)

    def "건강 데이터 저장 성공 테스트"() {
        given:
        HealthDataRequest request = createHealthDataRequest("test-record-key", 2)

        when:
        HealthDataSaveResponse response = healthDataService.saveHealthData(request)

        then:
        2 * healthEntryRepository.findByRecordKeyAndPeriodFromAndPeriodTo(_, _, _) >> Optional.empty()
        1 * healthEntryRepository.saveAll(_ as List) >> []
        1 * aggregationService.updateAggregations("test-record-key")

        response.savedCount == 2
        response.recordKey == "test-record-key"
    }

    def "건강 데이터 저장 테스트 - 빈 엔트리"() {
        given:
        HealthDataRequest request = new HealthDataRequest()
        request.recordKey = "test-record-key"
        request.data = new DataWrapper()
        request.data.entries = []

        when:
        HealthDataSaveResponse response = healthDataService.saveHealthData(request)

        then:
        0 * healthEntryRepository.findByRecordKeyAndPeriodFromAndPeriodTo(_, _, _)
        0 * healthEntryRepository.saveAll(_)
        0 * aggregationService.updateAggregations(_)

        response.savedCount == 0
    }

    def "건강 데이터 저장 테스트 - null 엔트리"() {
        given:
        HealthDataRequest request = new HealthDataRequest()
        request.recordKey = "test-record-key"
        request.data = new DataWrapper()
        request.data.entries = null

        when:
        HealthDataSaveResponse response = healthDataService.saveHealthData(request)

        then:
        0 * healthEntryRepository.saveAll(_)

        response.savedCount == 0
    }

    def "건강 데이터 업데이트 테스트 - 기존 데이터 존재"() {
        given:
        HealthDataRequest request = createHealthDataRequest("test-record-key", 1)

        HealthEntryEntity existingEntry = HealthEntryEntity.builder()
                .id(1L)
                .recordKey("test-record-key")
                .periodFrom(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .periodTo(LocalDateTime.of(2024, 1, 1, 10, 10, 0))
                .steps(100)
                .calories(5.0f)
                .distance(0.1f)
                .createdAt(LocalDateTime.now())
                .build()

        when:
        HealthDataSaveResponse response = healthDataService.saveHealthData(request)

        then:
        1 * healthEntryRepository.findByRecordKeyAndPeriodFromAndPeriodTo("test-record-key", _, _) >> Optional.of(existingEntry)
        1 * healthEntryRepository.saveAll(_ as List) >> [existingEntry]
        1 * aggregationService.updateAggregations("test-record-key")

        response.savedCount == 1
    }

    def "steps 값 Integer 타입 처리 테스트"() {
        given:
        EntryDto entry = new EntryDto()
        entry.steps = 1000

        when:
        Integer result = entry.getStepsAsInteger()

        then:
        result == 1000
    }

    def "steps 값 String 타입 처리 테스트"() {
        given:
        EntryDto entry = new EntryDto()
        entry.steps = "1500.7"

        when:
        Integer result = entry.getStepsAsInteger()

        then:
        result == 1501
    }

    def "steps 값 null 처리 테스트"() {
        given:
        EntryDto entry = new EntryDto()
        entry.steps = null

        when:
        Integer result = entry.getStepsAsInteger()

        then:
        result == 0
    }

    def "steps 값 잘못된 String 처리 테스트"() {
        given:
        EntryDto entry = new EntryDto()
        entry.steps = "invalid"

        when:
        Integer result = entry.getStepsAsInteger()

        then:
        result == 0
    }

    def "calories value Float 변환 테스트 - Number 타입"() {
        given:
        ValueDto valueDto = new ValueDto()
        valueDto.value = 123.45

        when:
        Float result = valueDto.getValueAsFloat()

        then:
        result == 123.45f
    }

    def "calories value Float 변환 테스트 - String 타입"() {
        given:
        ValueDto valueDto = new ValueDto()
        valueDto.value = "67.89"

        when:
        Float result = valueDto.getValueAsFloat()

        then:
        result == 67.89f
    }

    def "calories value Float 변환 테스트 - null 처리"() {
        given:
        ValueDto valueDto = new ValueDto()
        valueDto.value = null

        when:
        Float result = valueDto.getValueAsFloat()

        then:
        result == 0f
    }

    def "calories value Float 변환 테스트 - 잘못된 String 처리"() {
        given:
        ValueDto valueDto = new ValueDto()
        valueDto.value = "invalid"

        when:
        Float result = valueDto.getValueAsFloat()

        then:
        result == 0f
    }

    // Helper method
    private HealthDataRequest createHealthDataRequest(String recordKey, int entryCount) {
        HealthDataRequest request = new HealthDataRequest()
        request.recordKey = recordKey
        request.data = new DataWrapper()
        request.data.memo = "test memo"
        request.data.entries = []

        for (int i = 0; i < entryCount; i++) {
            EntryDto entry = new EntryDto()
            entry.period = new PeriodDto()
            entry.period.from = "2024-01-01 10:${String.format('%02d', i * 10)}:00"
            entry.period.to = "2024-01-01 10:${String.format('%02d', (i + 1) * 10)}:00"
            entry.steps = 100 + i
            entry.calories = new ValueDto()
            entry.calories.unit = "kcal"
            entry.calories.value = 5.0 + i
            entry.distance = new ValueDto()
            entry.distance.unit = "km"
            entry.distance.value = 0.1 + (i * 0.05)

            request.data.entries.add(entry)
        }

        return request
    }
}
