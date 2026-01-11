package com.ocare.domain.health.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataRequest {

    @NotBlank(message = "recordKey는 필수입니다")
    @JsonProperty("recordkey")
    private String recordKey;

    @NotNull(message = "data는 필수입니다")
    @Valid
    private DataWrapper data;
}
