package ammarfar.test.food.ordering.Entity;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {

  @Test
  void testCartCreation_Success() {
    User user = new User();
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    Cart cart = new Cart(user, product, 2);

    assertEquals(user, cart.getUser());
    assertEquals(product, cart.getProduct());
    assertEquals(2, cart.getQuantity());
  }

  @Test
  void testCartCreation_NullUser_ThrowsException() {
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new Cart(null, product, 2);
    });
    assertEquals("user cannot be empty", exception.getMessage());
  }

  @Test
  void testCartCreation_NullProduct_ThrowsException() {
    User user = new User();
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new Cart(user, null, 2);
    });
    assertEquals("product cannot be empty", exception.getMessage());
  }

  @Test
  void testCartCreation_NullQuantity_ThrowsException() {
    User user = new User();
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new Cart(user, product, null);
    });
    assertEquals("quantity cannot be empty", exception.getMessage());
  }

  @Test
  void testCartCreation_ZeroQuantity_ThrowsException() {
    User user = new User();
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new Cart(user, product, 0);
    });
    assertEquals("quantity must be greater than 0", exception.getMessage());
  }

  @Test
  void testCartCreation_NegativeQuantity_ThrowsException() {
    User user = new User();
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new Cart(user, product, -3);
    });
    assertEquals("quantity must be greater than 0", exception.getMessage());
  }

  @Test
  void testSubtotal() {
    User user = new User();
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    Cart cart = new Cart(user, product, 3);

    assertEquals(BigDecimal.valueOf(45000), cart.subtotal());
  }
}
