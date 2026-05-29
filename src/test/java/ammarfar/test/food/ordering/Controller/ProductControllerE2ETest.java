package ammarfar.test.food.ordering.Controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ammarfar.test.food.ordering.Dto.PageResponse;
import ammarfar.test.food.ordering.Dto.ProductRequest;
import ammarfar.test.food.ordering.Entity.Product;
import ammarfar.test.food.ordering.Repository.OrderRepository;
import ammarfar.test.food.ordering.Repository.ProductRepository;

@SpringBootTest
class ProductControllerE2ETest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private OrderRepository orderRepository;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();

    orderRepository.deleteAll();
    productRepository.deleteAll();
  }

  @Test
  void getProducts_returnsPagedResponse() throws Exception {
    productRepository.save(new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000)));

    mockMvc.perform(get("/api/products")
        .param("pageNo", "1")
        .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Products fetched"))
        .andExpect(jsonPath("$.data.content[0].name").value("Nasi Goreng"))
        .andExpect(jsonPath("$.data.content[0].description").value("Enak"))
        .andExpect(jsonPath("$.data.content[0].price").value(15000))
        .andExpect(jsonPath("$.data.pageNo").value(1))
        .andExpect(jsonPath("$.data.pageSize").value(10))
        .andExpect(jsonPath("$.data.totalElements").value(1))
        .andExpect(jsonPath("$.data.last").value(true));
  }

  @Test
  void getProducts_withPagination_returnsPartialPage() throws Exception {
    productRepository.save(new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000)));
    productRepository.save(new Product("Mie Ayam", "Enak", BigDecimal.valueOf(17000)));

    mockMvc.perform(get("/api/products")
        .param("pageNo", "1")
        .param("pageSize", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content", hasSize(1)))
        .andExpect(jsonPath("$.data.totalElements").value(2))
        .andExpect(jsonPath("$.data.totalPages").value(2))
        .andExpect(jsonPath("$.data.last").value(false));
  }

  @Test
  void getProducts_withNameFilter_returnsMatchedItems() throws Exception {
    productRepository.save(new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000)));
    productRepository.save(new Product("Mie Ayam", "Enak", BigDecimal.valueOf(17000)));

    mockMvc.perform(get("/api/products")
        .param("pageNo", "1")
        .param("pageSize", "10")
        .param("name", "nasi"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Products fetched"))
        .andExpect(jsonPath("$.data.content", hasSize(1)))
        .andExpect(jsonPath("$.data.content[0].name").value("Nasi Goreng"))
        .andExpect(jsonPath("$.data.totalElements").value(1))
        .andExpect(jsonPath("$.data.totalPages").value(1));
  }

  @Test
  void getProducts_withPriceRange_returnsMatchedItems() throws Exception {
    productRepository.save(new Product("Es Teh", "Segar", BigDecimal.valueOf(5000)));
    productRepository.save(new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000)));
    productRepository.save(new Product("Rendang", "Gurih", BigDecimal.valueOf(32000)));

    mockMvc.perform(get("/api/products")
        .param("pageNo", "1")
        .param("pageSize", "10")
        .param("minPrice", "10000")
        .param("maxPrice", "20000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Products fetched"))
        .andExpect(jsonPath("$.data.content", hasSize(1)))
        .andExpect(jsonPath("$.data.content[0].name").value("Nasi Goreng"))
        .andExpect(jsonPath("$.data.totalElements").value(1));
  }

  @Test
  void getProducts_withNameAndPriceRange_returnsMatchedItems() throws Exception {
    productRepository.save(new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000)));
    productRepository.save(new Product("Nasi Uduk", "Gurih", BigDecimal.valueOf(9000)));
    productRepository.save(new Product("Mie Ayam", "Enak", BigDecimal.valueOf(17000)));

    mockMvc.perform(get("/api/products")
        .param("pageNo", "1")
        .param("pageSize", "10")
        .param("name", "nasi")
        .param("minPrice", "10000")
        .param("maxPrice", "20000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Products fetched"))
        .andExpect(jsonPath("$.data.content", hasSize(1)))
        .andExpect(jsonPath("$.data.content[0].name").value("Nasi Goreng"))
        .andExpect(jsonPath("$.data.totalElements").value(1));
  }

  @Test
  void getProducts_withInvalidPriceRange_returnsBadRequest() throws Exception {
    mockMvc.perform(get("/api/products")
        .param("pageNo", "1")
        .param("pageSize", "10")
        .param("minPrice", "20000")
        .param("maxPrice", "10000"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("minPrice cannot be greater than maxPrice"));
  }

  @Test
  void getProductById_returnsProductResponse() throws Exception {
    Product product = productRepository.save(new Product("Mie Ayam", "Enak", BigDecimal.valueOf(17000)));

    mockMvc.perform(get("/api/products/{id}", product.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Product found"))
        .andExpect(jsonPath("$.data.id").value(product.getId()))
        .andExpect(jsonPath("$.data.name").value("Mie Ayam"))
        .andExpect(jsonPath("$.data.price").value(17000));
  }

  @Test
  void getProductById_whenMissing_returnsNotFound() throws Exception {
    mockMvc.perform(get("/api/products/{id}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = "ADMIN")
  void createUpdateDeleteProduct_flowWorks() throws Exception {
    String created = mockMvc.perform(post("/api/products")
        .contentType("application/json")
        .content("""
            {"name":"Es Teh","description":"Segar","price":5000}
            """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Product created"))
        .andExpect(jsonPath("$.data.name").value("Es Teh"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String id = created.replaceAll(".*\"id\":(\\d+).*", "$1");

    mockMvc.perform(put("/api/products/{id}", id)
        .contentType("application/json")
        .content("""
            {"name":"Es Teh Manis","description":"Lebih Segar","price":6000}
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Product updated"))
        .andExpect(jsonPath("$.data.name").value("Es Teh Manis"))
        .andExpect(jsonPath("$.data.price").value(6000));

    mockMvc.perform(delete("/api/products/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Product deleted"));
  }

  @Test
  void createProduct_withoutAuth_isForbidden() throws Exception {
    mockMvc.perform(post("/api/products")
        .contentType("application/json")
        .content("""
            {"name":"Es Teh","description":"Segar","price":5000}
            """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Unauthorized"));
  }

  @Test
  @WithMockUser(username = "user@example.com", roles = "USER")
  void createProduct_asNonAdmin_isForbidden() throws Exception {
    mockMvc.perform(post("/api/products")
        .contentType("application/json")
        .content("""
            {"name":"Es Teh","description":"Segar","price":5000}
            """))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Forbidden"));
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = "ADMIN")
  void updateMissingProduct_returnsNotFound() throws Exception {
    mockMvc.perform(put("/api/products/{id}", 999L)
        .contentType("application/json")
        .content("""
            {"name":"Es Teh Manis","description":"Lebih Segar","price":6000}
            """))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = "ADMIN")
  void deleteMissingProduct_returnsNotFound() throws Exception {
    mockMvc.perform(delete("/api/products/{id}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
  }
}
