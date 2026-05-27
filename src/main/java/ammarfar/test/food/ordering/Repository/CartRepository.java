package ammarfar.test.food.ordering.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ammarfar.test.food.ordering.Entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

}