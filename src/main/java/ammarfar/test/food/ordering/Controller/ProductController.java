package ammarfar.test.food.ordering.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ammarfar.test.food.ordering.Dto.ApiResponse;
import ammarfar.test.food.ordering.Dto.PageResponse;
import ammarfar.test.food.ordering.Dto.ProductRequest;
import ammarfar.test.food.ordering.Dto.ProductResponse;
import ammarfar.test.food.ordering.Entity.Product;
import ammarfar.test.food.ordering.Service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management endpoints")
@Validated
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Create product", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product created"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
  })
  public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
    return ApiResponse.of("Product created", ProductResponse.from(
        productService.createProduct(request)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Update product", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product updated"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
  })
  public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequest request) {
    return ApiResponse.of("Product updated", ProductResponse.from(
        productService.updateProduct(id, request)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Delete product", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product deleted"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
  })
  public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);

    return ApiResponse.of("Product deleted", null);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get product by id")
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
  })
  public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
    return ApiResponse.of("Product found", ProductResponse.from(
        productService.getProductById(id)));
  }

  @GetMapping
  @Operation(summary = "Get products", description = "List products with pagination")
  @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paged products"))
  public ApiResponse<PageResponse<ProductResponse>> getProducts(
      @Parameter(description = "Page number, 1-based") @RequestParam(defaultValue = "1") @Min(1) int pageNo,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") @Positive int pageSize,
      @Parameter(description = "Filter by product name")
      @RequestParam(required = false) String name) {
    int zeroBasedPageNo = Math.max(pageNo, 1) - 1;
    PageResponse<Product> response = productService.getProducts(zeroBasedPageNo, pageSize, name);
    PageResponse<ProductResponse> body = PageResponse.<ProductResponse>builder()
        .content(response.getContent().stream().map(ProductResponse::from).toList())
        .pageNo(response.getPageNo() + 1)
        .pageSize(response.getPageSize())
        .totalElements(response.getTotalElements())
        .totalPages(response.getTotalPages())
        .last(response.isLast())
        .build();

    return ApiResponse.of("Products fetched", body);
  }
}
