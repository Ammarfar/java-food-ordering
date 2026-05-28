package ammarfar.test.food.ordering.Dto;

import java.math.BigDecimal;

import ammarfar.test.food.ordering.Entity.Product;

public record ProductResponse(Long id, String name, String description, BigDecimal price) {
  public static ProductResponse from(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice());
  }
}
