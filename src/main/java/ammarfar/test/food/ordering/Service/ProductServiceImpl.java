package ammarfar.test.food.ordering.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ammarfar.test.food.ordering.Dto.PageResponse;
import ammarfar.test.food.ordering.Dto.ProductRequest;
import ammarfar.test.food.ordering.Entity.Product;
import ammarfar.test.food.ordering.Repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

  @Autowired
  private ProductRepository productRepository;

  @Override
  public Product createProduct(ProductRequest productRequest) {
    Product product = new Product(productRequest.name(), productRequest.description(), productRequest.price());
    return productRepository.save(product);
  }

  @Override
  public Product updateProduct(Long id, ProductRequest productRequest) {
    Product product = getProductById(id);
    product.update(productRequest.name(), productRequest.description(), productRequest.price());
    return productRepository.save(product);
  }

  @Override
  public void deleteProduct(Long id) {
    Product product = getProductById(id);
    productRepository.delete(product);
  }

  @Override
  public Product getProductById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
  }

  @Override
  public PageResponse<Product> getProducts(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo, pageSize);
    Page<Product> productsPage = productRepository.findAll(pageable);

    return PageResponse.<Product>builder()
        .content(productsPage.getContent())
        .pageNo(productsPage.getNumber())
        .pageSize(productsPage.getSize())
        .totalElements(productsPage.getTotalElements())
        .totalPages(productsPage.getTotalPages())
        .last(productsPage.isLast())
        .build();
  }
}
