package ammarfar.test.food.ordering.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ammarfar.test.food.ordering.Dto.ApiResponse;
import ammarfar.test.food.ordering.Dto.OrderItemRequest;
import ammarfar.test.food.ordering.Dto.OrderResponse;
import ammarfar.test.food.ordering.Dto.PageResponse;
import ammarfar.test.food.ordering.Entity.User;
import ammarfar.test.food.ordering.Repository.UserRepository;
import ammarfar.test.food.ordering.Service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order checkout and order history endpoints")
@Validated
public class OrderController {

  private final OrderService orderService;
  private final UserRepository userRepository;

  public OrderController(OrderService orderService, UserRepository userRepository) {
    this.orderService = orderService;
    this.userRepository = userRepository;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Place order", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order created"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid order payload"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public ApiResponse<OrderResponse> placeOrder(
      Authentication authentication,
      @RequestBody @Valid List<@Valid OrderItemRequest> items) {
    User user = getCurrentUser(authentication);

    return ApiResponse.of("Order created", OrderResponse.from(orderService.placeOrder(user, items)));
  }

  @GetMapping
  @Operation(summary = "Get my orders", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paged orders"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public ApiResponse<PageResponse<OrderResponse>> getOrders(
      Authentication authentication,
      @Parameter(description = "Page number, 1-based") @RequestParam(defaultValue = "1") @Min(1) int pageNo,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") @Positive int pageSize) {
    User user = getCurrentUser(authentication);
    int zeroBasedPageNo = Math.max(pageNo, 1) - 1;
    PageResponse<ammarfar.test.food.ordering.Entity.Order> response = orderService.getOrders(user, zeroBasedPageNo,
        pageSize);
    PageResponse<OrderResponse> body = PageResponse.<OrderResponse>builder()
        .content(response.getContent().stream().map(OrderResponse::from).toList())
        .pageNo(response.getPageNo() + 1)
        .pageSize(response.getPageSize())
        .totalElements(response.getTotalElements())
        .totalPages(response.getTotalPages())
        .last(response.isLast())
        .build();

    return ApiResponse.of("Orders fetched", body);
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Get all orders for admin", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paged orders"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
  })
  public ApiResponse<PageResponse<OrderResponse>> getOrdersForAdmin(
      @Parameter(description = "Page number, 1-based") @RequestParam(defaultValue = "1") @Min(1) int pageNo,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") @Positive int pageSize) {
    int zeroBasedPageNo = Math.max(pageNo, 1) - 1;
    PageResponse<ammarfar.test.food.ordering.Entity.Order> response = orderService.getOrdersForAdmin(zeroBasedPageNo,
        pageSize);
    PageResponse<OrderResponse> body = PageResponse.<OrderResponse>builder()
        .content(response.getContent().stream().map(OrderResponse::from).toList())
        .pageNo(response.getPageNo() + 1)
        .pageSize(response.getPageSize())
        .totalElements(response.getTotalElements())
        .totalPages(response.getTotalPages())
        .last(response.isLast())
        .build();

    return ApiResponse.of("Orders fetched", body);
  }

  private User getCurrentUser(Authentication authentication) {
    if (authentication == null || authentication.getName() == null) {
      throw new IllegalArgumentException("authenticated user cannot be empty");
    }

    return userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalArgumentException(
            "User not found with email: " + authentication.getName()));
  }
}
