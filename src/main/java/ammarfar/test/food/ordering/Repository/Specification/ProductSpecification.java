package ammarfar.test.food.ordering.Repository.Specification;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import ammarfar.test.food.ordering.Dto.ProductFilterRequest;
import ammarfar.test.food.ordering.Entity.Product;

public final class ProductSpecification {

  private ProductSpecification() {
  }

  public static Specification<Product> from(ProductFilterRequest filter) {
    ProductFilterRequest safeFilter = filter == null ? new ProductFilterRequest(null, null, null) : filter;
    Specification<Product> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

    if (safeFilter.name() != null) {
      specification = specification.and(nameContains(safeFilter.name()));
    }

    if (safeFilter.minPrice() != null) {
      specification = specification.and(priceGreaterThanOrEqualTo(safeFilter.minPrice()));
    }

    if (safeFilter.maxPrice() != null) {
      specification = specification.and(priceLessThanOrEqualTo(safeFilter.maxPrice()));
    }

    return specification;
  }

  private static Specification<Product> nameContains(String name) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(
        criteriaBuilder.lower(root.get("name")),
        "%" + name.toLowerCase(Locale.ROOT) + "%");
  }

  private static Specification<Product> priceGreaterThanOrEqualTo(BigDecimal minPrice) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
  }

  private static Specification<Product> priceLessThanOrEqualTo(BigDecimal maxPrice) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
  }
}
