package tn.casino.casino_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RouletteRequest {
    // NUMBER, RED, BLACK, EVEN, ODD
    @NotBlank private String betType;
    // Ykoun mawjoud ken betType = NUMBER (men 0 l 36)
    private Integer number;
    @NotNull private BigDecimal amount;
}