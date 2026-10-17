package tn.casino.casino_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DiceRequest {
    // UNDER wela OVER
    @NotBlank
    private String direction;
    // De 2 à 98
    @NotNull
    private Integer target;
    @NotNull
    private BigDecimal amount;
}