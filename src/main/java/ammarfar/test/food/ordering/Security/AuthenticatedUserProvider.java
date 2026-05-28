package ammarfar.test.food.ordering.Security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ammarfar.test.food.ordering.Entity.User;
import ammarfar.test.food.ordering.Repository.UserRepository;

@Component
public class AuthenticatedUserProvider {

  private final UserRepository userRepository;

  public AuthenticatedUserProvider(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getCurrentUser(Authentication authentication) {
    if (authentication == null || authentication.getName() == null) {
      throw new IllegalArgumentException("authenticated user cannot be empty");
    }

    return userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalArgumentException(
            "User not found with email: " + authentication.getName()));
  }
}
