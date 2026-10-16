package tn.casino.casino_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SlotsRequest {
    @NotNull private BigDecimal amount;
}