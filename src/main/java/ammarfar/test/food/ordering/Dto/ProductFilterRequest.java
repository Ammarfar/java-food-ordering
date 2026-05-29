package ammarfar.test.food.ordering.Dto;

import java.math.BigDecimal;

public record ProductFilterRequest(
    String name,
    BigDecimal minPrice,
    BigDecimal maxPrice) {

  public ProductFilterRequest {
    name = normalizeName(name);

    if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("minPrice cannot be negative");
    }

    if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("maxPrice cannot be negative");
    }

    if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
      throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
    }
  }

  private static String normalizeName(String name) {
    if (name == null || name.isBlank()) {
      return null;
    }

    return name.trim();
  }
}
