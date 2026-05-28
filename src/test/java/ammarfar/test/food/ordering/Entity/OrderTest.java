package ammarfar.test.food.ordering.Entity;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

  @Test
  void testOrderCreation_Success() {
    User user = new User();
    Order order = new Order(user);

    assertEquals(user, order.getUser());
    assertTrue(order.getItems().isEmpty());
    assertEquals(BigDecimal.ZERO, order.grandTotal());
    assertEquals(OrderStatus.PENDING, order.getStatus());
  }

  @Test
  void testOrderCreation_NullUser_ThrowsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new Order(null);
    });
    assertEquals("user cannot be empty", exception.getMessage());
  }

  @Test
  void testAddItem_Success() {
    User user = new User();
    Order order = new Order(user);
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));

    order.addItem(product, 2);

    assertEquals(1, order.getItems().size());
    OrderItem item = order.getItems().get(0);
    assertEquals(order, item.getOrder());
    assertEquals(2, item.getQuantity());
    assertEquals(BigDecimal.valueOf(15000), item.getPriceSnapshot());
    assertEquals("Nasi Goreng", item.getNameSnapshot());
  }

  @Test
  void testAddItem_NullProduct_ThrowsException() {
    User user = new User();
    Order order = new Order(user);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      order.addItem(null, 2);
    });
    assertEquals("product cannot be empty", exception.getMessage());
  }

  @Test
  void testGrandTotal_MultipleItems() {
    User user = new User();
    Order order = new Order(user);
    Product p1 = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    Product p2 = new Product("Es Teh", "Segar", BigDecimal.valueOf(5000));

    order.addItem(p1, 2); // 30000
    order.addItem(p2, 3); // 15000

    assertEquals(BigDecimal.valueOf(45000), order.grandTotal());
  }

  @Test
  void testUpdateStatus_Success() {
    User user = new User();
    Order order = new Order(user);
    order.updateStatus(OrderStatus.COMPLETED);
    assertEquals(OrderStatus.COMPLETED, order.getStatus());
  }

  @Test
  void testUpdateStatus_NullStatus_ThrowsException() {
    User user = new User();
    Order order = new Order(user);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      order.updateStatus(null);
    });
    assertEquals("status cannot be empty", exception.getMessage());
  }
}
