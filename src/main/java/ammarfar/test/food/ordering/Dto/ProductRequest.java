package ammarfar.test.food.ordering.Dto;

import java.math.BigDecimal;

public record ProductRequest(String name, String description, BigDecimal price) {
}
