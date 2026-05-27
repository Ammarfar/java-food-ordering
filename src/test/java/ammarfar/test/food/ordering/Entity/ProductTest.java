package ammarfar.test.food.ordering.Entity;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

  @Test
  void testProductCreation_Success() {
    Product product = new Product("Nasi Goreng", "Nasi goreng spesial", BigDecimal.valueOf(15000));
    assertEquals("Nasi Goreng", product.getName());
    assertEquals("Nasi goreng spesial", product.getDescription());
    assertEquals(BigDecimal.valueOf(15000), product.getPrice());
  }

  @Test
  void testProductCreation_NullName_ThrowsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new Product(null, "Nasi goreng", BigDecimal.valueOf(15000));
    });
    assertEquals("product name cannot be empty", exception.getMessage());
  }

  @Test
  void testProductCreation_BlankName_ThrowsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new Product("   ", "Nasi goreng", BigDecimal.valueOf(15000));
    });
    assertEquals("product name cannot be empty", exception.getMessage());
  }

  @Test
  void testProductCreation_NullPrice_ThrowsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new Product("Nasi Goreng", "Nasi goreng", null);
    });
    assertEquals("price cannot be empty", exception.getMessage());
  }

  @Test
  void testProductCreation_NegativePrice_ThrowsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new Product("Nasi Goreng", "Nasi goreng", BigDecimal.valueOf(-1));
    });
    assertEquals("price cannot be negative", exception.getMessage());
  }

  @Test
  void testProductUpdate_Success() {
    Product product = new Product("Nasi Goreng", "Nasi goreng spesial", BigDecimal.valueOf(15000));
    product.update("Nasi Goreng Gila", "Nasi goreng pedas", BigDecimal.valueOf(20000));

    assertEquals("Nasi Goreng Gila", product.getName());
    assertEquals("Nasi goreng pedas", product.getDescription());
    assertEquals(BigDecimal.valueOf(20000), product.getPrice());
  }

  @Test
  void testProductUpdate_InvalidName_ThrowsException() {
    Product product = new Product("Nasi Goreng", "Nasi goreng spesial", BigDecimal.valueOf(15000));
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      product.update("", "Nasi goreng pedas", BigDecimal.valueOf(20000));
    });
    assertEquals("product name cannot be empty", exception.getMessage());
  }
}
