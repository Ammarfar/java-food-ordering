package ammarfar.test.food.ordering.Security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import ammarfar.test.food.ordering.Entity.User;
import ammarfar.test.food.ordering.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserProviderTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AuthenticatedUserProvider authenticatedUserProvider;

  @Test
  void getCurrentUser_returnsUserFromAuthenticationName() {
    Authentication authentication = mock(Authentication.class);
    User user = createUser();

    when(authentication.getName()).thenReturn("user@example.com");
    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

    assertEquals(user, authenticatedUserProvider.getCurrentUser(authentication));
  }

  @Test
  void getCurrentUser_withoutAuthentication_throwsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> authenticatedUserProvider.getCurrentUser(null));

    assertEquals("authenticated user cannot be empty", exception.getMessage());
  }

  @Test
  void getCurrentUser_whenUserNotFound_throwsException() {
    Authentication authentication = mock(Authentication.class);

    when(authentication.getName()).thenReturn("missing@example.com");
    when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> authenticatedUserProvider.getCurrentUser(authentication));

    assertEquals("User not found with email: missing@example.com", exception.getMessage());
  }

  private User createUser() {
    try {
      var constructor = User.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
