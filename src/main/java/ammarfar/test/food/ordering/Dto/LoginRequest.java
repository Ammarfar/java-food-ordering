package ammarfar.test.food.ordering.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank @Schema(example = "user@example.com") String email,
    @NotBlank @Schema(example = "user") String password) {
}
