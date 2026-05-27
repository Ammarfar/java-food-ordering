package ammarfar.test.food.ordering.Entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

  @Test
  void testIsAdmin() {
    User user = new User();

    assertNull(user.getRole());
    assertFalse(user.isAdmin());

    ReflectionTestUtils.setField(user, "role", Role.ADMIN);
    assertTrue(user.isAdmin());

    ReflectionTestUtils.setField(user, "role", Role.USER);
    assertFalse(user.isAdmin());
  }
}
