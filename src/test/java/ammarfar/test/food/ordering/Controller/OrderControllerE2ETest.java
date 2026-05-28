package ammarfar.test.food.ordering.Controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ammarfar.test.food.ordering.Dto.LoginRequest;
import ammarfar.test.food.ordering.Entity.Product;
import ammarfar.test.food.ordering.Entity.Role;
import ammarfar.test.food.ordering.Entity.User;
import ammarfar.test.food.ordering.Repository.OrderRepository;
import ammarfar.test.food.ordering.Repository.ProductRepository;
import ammarfar.test.food.ordering.Repository.UserRepository;

@SpringBootTest
class OrderControllerE2ETest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private UserRepository userRepository;

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
    userRepository.deleteAll();
  }

  @Test
  @WithMockUser(username = "user@example.com", roles = "USER")
  void placeOrder_returnsCreatedOrder() throws Exception {
    createUser("user@example.com", Role.USER);
    Product product = productRepository.save(new Product(
        "Nasi Goreng",
        "Enak",
        BigDecimal.valueOf(15000)));

    mockMvc.perform(post("/api/orders")
        .contentType("application/json")
        .content("""
            [{"productId":%d,"quantity":2}]
            """.formatted(product.getId())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Order created"))
        .andExpect(jsonPath("$.data.userId").exists())
        .andExpect(jsonPath("$.data.status").value("PENDING"))
        .andExpect(jsonPath("$.data.items", hasSize(1)))
        .andExpect(jsonPath("$.data.items[0].quantity").value(2))
        .andExpect(jsonPath("$.data.items[0].nameSnapshot").value("Nasi Goreng"));
  }

  @Test
  @WithMockUser(username = "user@example.com", roles = "USER")
  void placeOrder_emptyItems_returnsBadRequest() throws Exception {
    createUser("user@example.com", Role.USER);

    mockMvc.perform(post("/api/orders")
        .contentType("application/json")
        .content("[]"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Cannot place order with an empty items list"));
  }

  @Test
  @WithMockUser(username = "user@example.com", roles = "USER")
  void getOrders_returnsPagedResponse() throws Exception {
    createUser("user@example.com", Role.USER);
    Product product = productRepository.save(new Product(
        "Mie Ayam",
        "Enak",
        BigDecimal.valueOf(17000)));

    mockMvc.perform(post("/api/orders")
        .contentType("application/json")
        .content("""
            [{"productId":%d,"quantity":1}]
            """.formatted(product.getId())))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/orders")
        .param("pageNo", "1")
        .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Orders fetched"))
        .andExpect(jsonPath("$.data.pageNo").value(1))
        .andExpect(jsonPath("$.data.pageSize").value(10))
        .andExpect(jsonPath("$.data.totalElements").value(1))
        .andExpect(jsonPath("$.data.last").value(true))
        .andExpect(jsonPath("$.data.content", hasSize(1)))
        .andExpect(jsonPath("$.data.content[0].userId").exists())
        .andExpect(jsonPath("$.data.content[0].items[0].nameSnapshot").value("Mie Ayam"));
  }

  @Test
  void getOrders_withoutAuth_returnsUnauthorized() throws Exception {
    mockMvc.perform(get("/api/orders")
        .param("pageNo", "1")
        .param("pageSize", "10"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Unauthorized"));
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = "ADMIN")
  void getOrdersForAdmin_returnsPagedResponse() throws Exception {
    createUser("admin@example.com", Role.ADMIN);
    Product product = productRepository.save(new Product(
        "Es Teh",
        "Segar",
        BigDecimal.valueOf(5000)));
    createOrderFor(product.getId(), 3);

    mockMvc.perform(get("/api/orders/admin")
        .param("pageNo", "1")
        .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Orders fetched"))
        .andExpect(jsonPath("$.data.pageNo").value(1))
        .andExpect(jsonPath("$.data.pageSize").value(10))
        .andExpect(jsonPath("$.data.totalElements").value(1))
        .andExpect(jsonPath("$.data.content", hasSize(1)))
        .andExpect(jsonPath("$.data.content[0].userId").exists())
        .andExpect(jsonPath("$.data.content[0].items[0].nameSnapshot").value("Es Teh"));
  }

  @Test
  @WithMockUser(username = "user@example.com", roles = "USER")
  void getOrdersForAdmin_asNonAdmin_returnsForbidden() throws Exception {
    mockMvc.perform(get("/api/orders/admin")
        .param("pageNo", "1")
        .param("pageSize", "10"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Forbidden"));
  }

  private void createUser(String email, Role role) {
    userRepository.save(buildUser(email, role));
  }

  private void createOrderFor(Long productId, int quantity) throws Exception {
    mockMvc.perform(post("/api/orders")
        .contentType("application/json")
        .content("""
            [{"productId":%d,"quantity":%d}]
            """.formatted(productId, quantity)))
        .andExpect(status().isCreated());
  }

  private User buildUser(String email, Role role) {
    try {
      var constructor = User.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      User user = constructor.newInstance();
      setField(user, "name", email);
      setField(user, "email", email);
      setField(user, "password", "password");
      setField(user, "role", role);
      return user;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void setField(Object target, String fieldName, Object value) {
    try {
      var field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
