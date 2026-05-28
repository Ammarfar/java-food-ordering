package ammarfar.test.food.ordering.Service;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import ammarfar.test.food.ordering.Entity.Role;
import ammarfar.test.food.ordering.Entity.User;
import ammarfar.test.food.ordering.Repository.UserRepository;
import ammarfar.test.food.ordering.Security.JwtTokenProvider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtTokenProvider tokenProvider;

  @InjectMocks
  private UserServiceImpl userService;

  private User createUser() {
    try {
      java.lang.reflect.Constructor<User> constructor = User.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testLoadUserByUsername_Success() {
    User user = createUser();
    ReflectionTestUtils.setField(user, "email", "user@example.com");
    ReflectionTestUtils.setField(user, "password", "hashedPassword");
    ReflectionTestUtils.setField(user, "role", Role.USER);

    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

    UserDetails userDetails = userService.loadUserByUsername("user@example.com");

    assertNotNull(userDetails);
    assertEquals("user@example.com", userDetails.getUsername());
    assertEquals("hashedPassword", userDetails.getPassword());
    assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
  }

  @Test
  void testLoadUserByUsername_NotFound_ThrowsException() {
    when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> {
      userService.loadUserByUsername("unknown@example.com");
    });
  }

  @Test
  void testLogin_Success() {
    User user = createUser();
    ReflectionTestUtils.setField(user, "email", "user@example.com");
    ReflectionTestUtils.setField(user, "password", "hashedPassword");

    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("rawPassword", "hashedPassword")).thenReturn(true);
    when(tokenProvider.generateToken("user@example.com")).thenReturn("dummyJwtToken");

    String token = userService.login("user@example.com", "rawPassword");

    assertEquals("dummyJwtToken", token);
  }

  @Test
  void testLogin_WrongPassword_ThrowsException() {
    User user = createUser();
    ReflectionTestUtils.setField(user, "email", "user@example.com");
    ReflectionTestUtils.setField(user, "password", "hashedPassword");

    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.login("user@example.com", "wrongPassword");
    });
    assertEquals("Invalid email or password", exception.getMessage());
  }

  @Test
  void testLogin_UserNotFound_ThrowsException() {
    when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.login("missing@example.com", "password");
    });
    assertEquals("Invalid email or password", exception.getMessage());
  }
}
