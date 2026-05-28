package ammarfar.test.food.ordering.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ammarfar.test.food.ordering.Entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

}