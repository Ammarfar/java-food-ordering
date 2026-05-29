package ammarfar.test.food.ordering.Repository.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import ammarfar.test.food.ordering.Dto.ProductFilterRequest;
import ammarfar.test.food.ordering.Entity.Product;
import ammarfar.test.food.ordering.Repository.ProductRepository;

@SpringBootTest
class ProductSpecificationTest {

  @Autowired
  private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
    productRepository.save(new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000)));
    productRepository.save(new Product("Nasi Uduk", "Gurih", BigDecimal.valueOf(9000)));
    productRepository.save(new Product("Mie Ayam", "Enak", BigDecimal.valueOf(17000)));
    productRepository.save(new Product("Rendang", "Gurih", BigDecimal.valueOf(32000)));
  }

  @Test
  void from_withoutFilter_returnsAllProducts() {
    var products = productRepository.findAll(ProductSpecification.from(null), Pageable.unpaged());

    assertEquals(4, products.getTotalElements());
  }

  @Test
  void from_withName_returnsCaseInsensitiveMatches() {
    var products = productRepository.findAll(
        ProductSpecification.from(new ProductFilterRequest("NASI", null, null)),
        Pageable.unpaged());

    assertEquals(2, products.getTotalElements());
  }

  @Test
  void from_withMinPrice_returnsProductsAtOrAbovePrice() {
    var products = productRepository.findAll(
        ProductSpecification.from(new ProductFilterRequest(null, BigDecimal.valueOf(17000), null)),
        Pageable.unpaged());

    assertEquals(2, products.getTotalElements());
  }

  @Test
  void from_withMaxPrice_returnsProductsAtOrBelowPrice() {
    var products = productRepository.findAll(
        ProductSpecification.from(new ProductFilterRequest(null, null, BigDecimal.valueOf(15000))),
        Pageable.unpaged());

    assertEquals(2, products.getTotalElements());
  }

  @Test
  void from_withNameAndPriceRange_returnsCombinedMatches() {
    var products = productRepository.findAll(
        ProductSpecification.from(
            new ProductFilterRequest("nasi", BigDecimal.valueOf(10000), BigDecimal.valueOf(20000))),
        Pageable.unpaged());

    assertEquals(1, products.getTotalElements());
    assertEquals("Nasi Goreng", products.getContent().get(0).getName());
  }
}
