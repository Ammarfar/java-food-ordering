package ammarfar.test.food.ordering.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ammarfar.test.food.ordering.Entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}