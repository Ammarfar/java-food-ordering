package ammarfar.test.food.ordering.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ammarfar.test.food.ordering.Entity.Order;
import ammarfar.test.food.ordering.Entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  Page<Order> findByUser(User user, Pageable page);
}