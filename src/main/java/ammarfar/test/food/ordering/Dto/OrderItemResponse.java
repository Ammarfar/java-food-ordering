package ammarfar.test.food.ordering.Dto;

import java.math.BigDecimal;

import ammarfar.test.food.ordering.Entity.OrderItem;

public record OrderItemResponse(Long id, Integer quantity, BigDecimal priceSnapshot, String nameSnapshot) {
  public static OrderItemResponse from(OrderItem item) {
    return new OrderItemResponse(
        item.getId(),
        item.getQuantity(),
        item.getPriceSnapshot(),
        item.getNameSnapshot());
  }
}
