package ammarfar.test.food.ordering.Entity;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

  @Test
  void testOrderItemCreation_Success() {
    User user = new User();
    Order order = new Order(user);
    OrderItem item = new OrderItem(order, 2, BigDecimal.valueOf(15000), "Nasi Goreng");

    assertEquals(order, item.getOrder());
    assertEquals(2, item.getQuantity());
    assertEquals(BigDecimal.valueOf(15000), item.getPriceSnapshot());
    assertEquals("Nasi Goreng", item.getNameSnapshot());
  }

  @Test
  void testOrderItemCreation_NullOrder_ThrowsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new OrderItem(null, 2, BigDecimal.valueOf(15000), "Nasi Goreng");
    });
    assertEquals("order cannot be empty", exception.getMessage());
  }

  @Test
  void testOrderItemCreation_NullQuantity_ThrowsException() {
    User user = new User();
    Order order = new Order(user);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new OrderItem(order, null, BigDecimal.valueOf(15000), "Nasi Goreng");
    });
    assertEquals("quantity cannot be empty", exception.getMessage());
  }

  @Test
  void testOrderItemCreation_ZeroQuantity_ThrowsException() {
    User user = new User();
    Order order = new Order(user);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new OrderItem(order, 0, BigDecimal.valueOf(15000), "Nasi Goreng");
    });
    assertEquals("quantity must be greater than 0", exception.getMessage());
  }

  @Test
  void testOrderItemCreation_NegativeQuantity_ThrowsException() {
    User user = new User();
    Order order = new Order(user);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new OrderItem(order, -1, BigDecimal.valueOf(15000), "Nasi Goreng");
    });
    assertEquals("quantity must be greater than 0", exception.getMessage());
  }

  @Test
  void testOrderItemCreation_NullPrice_ThrowsException() {
    User user = new User();
    Order order = new Order(user);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new OrderItem(order, 2, null, "Nasi Goreng");
    });
    assertEquals("price cannot be empty", exception.getMessage());
  }

  @Test
  void testOrderItemCreation_NegativePrice_ThrowsException() {
    User user = new User();
    Order order = new Order(user);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new OrderItem(order, 2, BigDecimal.valueOf(-15000), "Nasi Goreng");
    });
    assertEquals("price cannot be negative", exception.getMessage());
  }

  @Test
  void testOrderItemCreation_NullName_ThrowsException() {
    User user = new User();
    Order order = new Order(user);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new OrderItem(order, 2, BigDecimal.valueOf(15000), null);
    });
    assertEquals("product name cannot be empty", exception.getMessage());
  }

  @Test
  void testOrderItemCreation_BlankName_ThrowsException() {
    User user = new User();
    Order order = new Order(user);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new OrderItem(order, 2, BigDecimal.valueOf(15000), "   ");
    });
    assertEquals("product name cannot be empty", exception.getMessage());
  }

  @Test
  void testSubtotal() {
    User user = new User();
    Order order = new Order(user);
    OrderItem item = new OrderItem(order, 3, BigDecimal.valueOf(15000), "Nasi Goreng");

    assertEquals(BigDecimal.valueOf(45000), item.subtotal());
  }
}
