package com.ocare.domain.health.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DataWrapper {
    private String memo;

    @Valid
    private List<EntryDto> entries;
}
