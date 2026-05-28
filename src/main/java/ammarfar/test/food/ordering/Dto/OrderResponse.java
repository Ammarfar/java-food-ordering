package ammarfar.test.food.ordering.Dto;

import java.util.List;

import ammarfar.test.food.ordering.Entity.Order;

public record OrderResponse(Long id, Long userId, String status, List<OrderItemResponse> items) {
  public static OrderResponse from(Order order) {
    return new OrderResponse(
        order.getId(),
        order.getUser().getId(),
        order.getStatus().name(),
        order.getItems().stream().map(OrderItemResponse::from).toList());
  }
}
