package com.ocare.domain.health.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataRequest {

    @JsonProperty("recordkey")
    private String recordKey;

    private DataWrapper data;
}
