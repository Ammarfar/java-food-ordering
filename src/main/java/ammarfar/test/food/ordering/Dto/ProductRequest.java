package ammarfar.test.food.ordering.Dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequest(
    @NotBlank @Schema(example = "Ice Tea") String name,
    String description,
    @NotNull @PositiveOrZero @Schema(example = "5000") BigDecimal price) {
}
