package ammarfar.test.food.ordering.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ammarfar.test.food.ordering.Dto.PageResponse;
import ammarfar.test.food.ordering.Dto.ProductRequest;
import ammarfar.test.food.ordering.Entity.Product;
import ammarfar.test.food.ordering.Repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductServiceImpl productService;

  @Test
  void testCreateProduct_Success() {
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    when(productRepository.save(any(Product.class))).thenReturn(product);

    Product created = productService
        .createProduct(new ProductRequest("Nasi Goreng", "Enak", BigDecimal.valueOf(15000)));

    assertNotNull(created);
    assertEquals("Nasi Goreng", created.getName());
    verify(productRepository, times(1)).save(any(Product.class));
  }

  @Test
  void testCreateProduct_InvalidName_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> productService.createProduct(new ProductRequest("", "Enak", BigDecimal.valueOf(15000))));
  }

  @Test
  void testCreateProduct_NegativePrice_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> productService.createProduct(new ProductRequest("Nasi Goreng", "Enak", BigDecimal.valueOf(-1))));
  }

  @Test
  void testGetProductById_Success() {
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    Product found = productService.getProductById(1L);

    assertNotNull(found);
    assertEquals("Nasi Goreng", found.getName());
  }

  @Test
  void testGetProductById_NotFound_ThrowsException() {
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> {
      productService.getProductById(1L);
    });
  }

  @Test
  void testUpdateProduct_Success() {
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);

    Product updated = productService.updateProduct(1L,
        new ProductRequest("Nasi Goreng Spesial", "Lebih Enak", BigDecimal.valueOf(18000)));

    assertNotNull(updated);
    assertEquals("Nasi Goreng Spesial", updated.getName());
    assertEquals("Lebih Enak", updated.getDescription());
    assertEquals(BigDecimal.valueOf(18000), updated.getPrice());
  }

  @Test
  void testUpdateProduct_NotFound_ThrowsException() {
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L,
        new ProductRequest("Nasi Goreng Spesial", "Lebih Enak", BigDecimal.valueOf(18000))));
  }

  @Test
  void testUpdateProduct_InvalidPrice_ThrowsException() {
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L,
        new ProductRequest("Nasi Goreng Spesial", "Lebih Enak", BigDecimal.valueOf(-1))));
  }

  @Test
  void testDeleteProduct_Success() {
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    doNothing().when(productRepository).delete(product);

    productService.deleteProduct(1L);

    verify(productRepository, times(1)).delete(product);
  }

  @Test
  void testDeleteProduct_NotFound_ThrowsException() {
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(1L));
  }

  @Test
  void testGetProducts_Pagination() {
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> page = new PageImpl<>(Collections.singletonList(product), pageable, 1);

    when(productRepository.findAll(pageable)).thenReturn(page);

    PageResponse<Product> response = productService.getProducts(0, 10, null);

    assertNotNull(response);
    assertEquals(1, response.getContent().size());
    assertEquals(0, response.getPageNo());
    assertEquals(10, response.getPageSize());
    assertEquals(1, response.getTotalElements());
    assertEquals(1, response.getTotalPages());
    assertTrue(response.isLast());
  }

  @Test
  void testGetProducts_BlankName() {
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> page = new PageImpl<>(Collections.singletonList(product), pageable, 1);

    when(productRepository.findAll(pageable)).thenReturn(page);

    PageResponse<Product> response = productService.getProducts(0, 10, " ");

    assertNotNull(response);
    assertEquals(1, response.getContent().size());
    verify(productRepository, times(1)).findAll(pageable);
  }

  @Test
  void testGetProducts_FilterByName() {
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> page = new PageImpl<>(Collections.singletonList(product), pageable, 1);

    when(productRepository.findByNameContainingIgnoreCase("nasi", pageable)).thenReturn(page);

    PageResponse<Product> response = productService.getProducts(0, 10, "nasi");

    assertNotNull(response);
    assertEquals(1, response.getContent().size());
    verify(productRepository, times(1)).findByNameContainingIgnoreCase("nasi", pageable);
  }
}
