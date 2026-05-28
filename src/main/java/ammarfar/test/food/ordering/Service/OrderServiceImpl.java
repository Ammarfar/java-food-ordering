package ammarfar.test.food.ordering.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ammarfar.test.food.ordering.Dto.OrderItemRequest;
import ammarfar.test.food.ordering.Dto.PageResponse;
import ammarfar.test.food.ordering.Entity.Order;
import ammarfar.test.food.ordering.Entity.OrderStatus;
import ammarfar.test.food.ordering.Entity.Product;
import ammarfar.test.food.ordering.Entity.User;
import ammarfar.test.food.ordering.Repository.OrderRepository;
import ammarfar.test.food.ordering.Repository.ProductRepository;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProductRepository productRepository;

  @Override
  public Order placeOrder(User user, List<OrderItemRequest> items) {
    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("Cannot place order with an empty items list");
    }

    Set<Long> productIds = new LinkedHashSet<>();
    for (OrderItemRequest item : items) {
      if (item == null) {
        throw new IllegalArgumentException("order item cannot be empty");
      }

      if (item.productId() == null) {
        throw new IllegalArgumentException("product id cannot be empty");
      }

      if (item.quantity() == null) {
        throw new IllegalArgumentException("quantity cannot be empty");
      }

      if (item.quantity() <= 0) {
        throw new IllegalArgumentException("quantity must be greater than 0");
      }

      productIds.add(item.productId());
    }

    // find & validate products
    Map<Long, Product> productsById = productRepository
        .findAllById(productIds)
        .stream()
        .collect(Collectors.toMap(
            Product::getId,
            Function.identity()));

    Order order = new Order(user);
    for (OrderItemRequest item : items) {
      Product product = productsById.get(
          item.productId());

      if (product == null) {
        throw new IllegalArgumentException(
            "Product not found with id: "
                + item.productId());
      }

      order.addItem(
          product,
          item.quantity());
    }

    return orderRepository.save(order);
  }

  @Override
  public PageResponse<Order> getOrders(User user, int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo, pageSize);
    Page<Order> ordersPage = orderRepository.findByUser(user, pageable);

    return PageResponse.<Order>builder()
        .content(ordersPage.getContent())
        .pageNo(ordersPage.getNumber())
        .pageSize(ordersPage.getSize())
        .totalElements(ordersPage.getTotalElements())
        .totalPages(ordersPage.getTotalPages())
        .last(ordersPage.isLast())
        .build();
  }

  @Override
  public PageResponse<Order> getOrdersForAdmin(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo, pageSize);
    Page<Order> ordersPage = orderRepository.findAll(pageable);

    return PageResponse.<Order>builder()
        .content(ordersPage.getContent())
        .pageNo(ordersPage.getNumber())
        .pageSize(ordersPage.getSize())
        .totalElements(ordersPage.getTotalElements())
        .totalPages(ordersPage.getTotalPages())
        .last(ordersPage.isLast())
        .build();
  }

  @Override
  public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

    order.updateStatus(newStatus);
    return orderRepository.save(order);
  }
}
