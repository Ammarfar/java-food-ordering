package ammarfar.test.food.ordering.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ammarfar.test.food.ordering.Entity.Role;
import ammarfar.test.food.ordering.Entity.User;
import ammarfar.test.food.ordering.Repository.UserRepository;

@SpringBootTest
class AuthControllerE2ETest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();

    userRepository.deleteAll();
    userRepository.save(buildUser("admin@example.com", passwordEncoder.encode("admin123"), Role.ADMIN));
  }

  @Test
  void login_returnsJwtToken() throws Exception {
    mockMvc.perform(post("/api/auth/login")
        .contentType("application/json")
        .content("""
            {"email":"admin@example.com","password":"admin123"}
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Login successful"))
        .andExpect(jsonPath("$.data.token").exists());
  }

  @Test
  void login_withWrongPassword_returnsUnauthorized() throws Exception {
    mockMvc.perform(post("/api/auth/login")
        .contentType("application/json")
        .content("""
            {"email":"admin@example.com","password":"wrong"}
            """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid email or password"));
  }

  private User buildUser(String email, String password, Role role) {
    try {
      var constructor = User.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      User user = constructor.newInstance();
      setField(user, "name", email);
      setField(user, "email", email);
      setField(user, "password", password);
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
