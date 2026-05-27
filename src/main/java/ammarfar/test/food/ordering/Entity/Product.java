package ammarfar.test.food.ordering.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private BigDecimal price;

  public Product(
      String name,
      String description,
      BigDecimal price) {
    validate(name, price);

    this.name = name;
    this.description = description;
    this.price = price;
  }

  private void validate(
      String name,
      BigDecimal price) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("product name cannot be empty");
    }

    if (price == null) {
      throw new IllegalArgumentException("price cannot be empty");
    }

    if (price.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("price cannot be negative");
    }
  }

  public void update(
      String name,
      String description,
      BigDecimal price) {
    validate(name, price);

    this.name = name;
    this.description = description;
    this.price = price;
  }
}