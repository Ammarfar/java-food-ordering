package ammarfar.test.food.ordering.Dto;

import ammarfar.test.food.ordering.Entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
    @NotNull @Schema(example = "PREPARING") OrderStatus status) {
}
