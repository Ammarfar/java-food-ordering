package ammarfar.test.food.ordering.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Order order;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private BigDecimal priceSnapshot;

  @Column(nullable = false)
  private String nameSnapshot;

  public OrderItem(
      Order order,
      Integer quantity,
      BigDecimal priceSnapshot,
      String nameSnapshot) {
    validate(order, quantity, priceSnapshot, nameSnapshot);

    this.order = order;
    this.quantity = quantity;
    this.priceSnapshot = priceSnapshot;
    this.nameSnapshot = nameSnapshot;
  }

  private void validate(
      Order order,
      Integer quantity,
      BigDecimal price,
      String name) {
    if (order == null) {
      throw new IllegalArgumentException("order cannot be empty");
    }

    if (quantity == null) {
      throw new IllegalArgumentException("quantity cannot be empty");
    }

    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity must be greater than 0");
    }

    if (price == null) {
      throw new IllegalArgumentException("price cannot be empty");
    }

    if (price.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("price cannot be negative");
    }

    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("product name cannot be empty");
    }
  }

  public BigDecimal subtotal() {
    return priceSnapshot
        .multiply(BigDecimal.valueOf(quantity));
  }
}