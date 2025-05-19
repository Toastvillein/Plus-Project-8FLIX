package com.example.eightflix.domain.review.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @Size(min = 1, max = 300, message = "내용은 1자 이상 300자 이하로 입력해주세요.")
        String contents,

        @NotNull(message = "별점을 입력해주세요.")
        @DecimalMin(value = "0.0", message = "최소 평점은 0.0입니다.")
        @DecimalMax(value = "5.0", message = "최대 평점은 5.0입니다.")
        Float rate
) {

}
