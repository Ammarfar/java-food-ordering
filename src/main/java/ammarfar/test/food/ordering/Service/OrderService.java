package ammarfar.test.food.ordering.Service;

import java.util.List;

import ammarfar.test.food.ordering.Dto.OrderItemRequest;
import ammarfar.test.food.ordering.Dto.PageResponse;
import ammarfar.test.food.ordering.Entity.Order;
import ammarfar.test.food.ordering.Entity.OrderStatus;
import ammarfar.test.food.ordering.Entity.User;

public interface OrderService {
  Order placeOrder(User user, List<OrderItemRequest> items);

  PageResponse<Order> getOrders(User user, int pageNo, int pageSize);

  PageResponse<Order> getOrdersForAdmin(int pageNo, int pageSize);

  Order updateOrderStatus(Long orderId, OrderStatus newStatus);
}
