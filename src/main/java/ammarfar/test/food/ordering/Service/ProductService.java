package ammarfar.test.food.ordering.Service;

import ammarfar.test.food.ordering.Dto.PageResponse;
import ammarfar.test.food.ordering.Dto.ProductRequest;
import ammarfar.test.food.ordering.Entity.Product;

public interface ProductService {
  Product createProduct(ProductRequest productRequest);

  Product updateProduct(Long id, ProductRequest productRequest);

  void deleteProduct(Long id);

  Product getProductById(Long id);

  PageResponse<Product> getProducts(int pageNo, int pageSize);
}
