package com.ocare.domain.health.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodDto {

    @NotBlank(message = "from은 필수입니다")
    private String from;

    @NotBlank(message = "to는 필수입니다")
    private String to;
}
