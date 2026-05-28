package ammarfar.test.food.ordering.Service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
  String login(String email, String password);
}
