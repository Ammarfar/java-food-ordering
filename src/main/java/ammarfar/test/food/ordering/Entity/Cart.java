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
@Table(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Product product;

  @Column(nullable = false)
  private Integer quantity;

  public Cart(User user, Product product, Integer quantity) {
    validate(user, product, quantity);

    this.user = user;
    this.product = product;
    this.quantity = quantity;
  }

  private void validate(
      User user,
      Product product,
      Integer quantity) {
    if (user == null) {
      throw new IllegalArgumentException("user cannot be empty");
    }

    if (product == null) {
      throw new IllegalArgumentException("product cannot be empty");
    }

    if (quantity == null) {
      throw new IllegalArgumentException("quantity cannot be empty");
    }

    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity must be greater than 0");
    }
  }

  public BigDecimal subtotal() {
    return product.getPrice()
        .multiply(BigDecimal.valueOf(quantity));
  }
}