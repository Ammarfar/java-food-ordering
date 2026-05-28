package ammarfar.test.food.ordering.Entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItem> items = new ArrayList<>();

  public Order(User user) {
    validate(user);

    this.user = user;
    this.status = OrderStatus.PENDING;
  }

  private void validate(User user) {
    if (user == null) {
      throw new IllegalArgumentException("user cannot be empty");
    }
  }

  public void addItem(
      Product product,
      int quantity) {
    if (product == null) {
      throw new IllegalArgumentException("product cannot be empty");
    }

    items.add(
        new OrderItem(
            this,
            quantity,
            product.getPrice(),
            product.getName()));
  }

  public BigDecimal grandTotal() {
    return items.stream()
        .map(OrderItem::subtotal)
        .reduce(
            BigDecimal.ZERO,
            BigDecimal::add);
  }

  public void updateStatus(OrderStatus newStatus) {
    if (newStatus == null) {
      throw new IllegalArgumentException("status cannot be empty");
    }
    this.status = newStatus;
  }
}