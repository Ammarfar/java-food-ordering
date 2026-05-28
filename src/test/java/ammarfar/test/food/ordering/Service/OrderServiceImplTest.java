package ammarfar.test.food.ordering.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import ammarfar.test.food.ordering.Dto.OrderItemRequest;
import ammarfar.test.food.ordering.Dto.PageResponse;
import ammarfar.test.food.ordering.Entity.Order;
import ammarfar.test.food.ordering.Entity.OrderStatus;
import ammarfar.test.food.ordering.Entity.Product;
import ammarfar.test.food.ordering.Entity.User;
import ammarfar.test.food.ordering.Repository.OrderRepository;
import ammarfar.test.food.ordering.Repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private OrderServiceImpl orderService;

  private User createUser() {
    try {
      java.lang.reflect.Constructor<User> constructor = User.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testPlaceOrder_Success() {
    User user = createUser();
    Product product = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    ReflectionTestUtils.setField(product, "id", 1L);
    doReturn(List.of(product)).when(productRepository).findAllById((Iterable<Long>) any());
    when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

    Order order = orderService.placeOrder(
        user,
        List.of(new OrderItemRequest(1L, 2)));

    assertNotNull(order);
    assertEquals(user, order.getUser());
    assertEquals(1, order.getItems().size());
    assertEquals(OrderStatus.PENDING, order.getStatus());
    assertEquals(BigDecimal.valueOf(30000), order.grandTotal());
    verify(productRepository, times(1)).findAllById((Iterable<Long>) any());
    verify(orderRepository, times(1)).save(any(Order.class));
  }

  @Test
  void testPlaceOrder_EmptyItems_ThrowsException() {
    User user = createUser();

    assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(user, List.of()));
  }

  @Test
  void testPlaceOrder_NullItems_ThrowsException() {
    User user = createUser();

    assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(user, null));
  }

  @Test
  void testPlaceOrder_InvalidQuantity_ThrowsException() {
    User user = createUser();

    assertThrows(IllegalArgumentException.class,
        () -> orderService.placeOrder(user, List.of(new OrderItemRequest(1L, 0))));
  }

  @Test
  void testPlaceOrder_ProductNotFound_ThrowsException() {
    User user = createUser();
    doReturn(List.of()).when(productRepository).findAllById((Iterable<Long>) any());

    assertThrows(IllegalArgumentException.class,
        () -> orderService.placeOrder(user, List.of(new OrderItemRequest(1L, 1))));
  }

  @Test
  void testPlaceOrder_NullItem_ThrowsException() {
    User user = createUser();
    List<OrderItemRequest> items = new ArrayList<>();
    items.add(null);

    assertThrows(IllegalArgumentException.class,
        () -> orderService.placeOrder(user, items));
  }

  @Test
  void testPlaceOrder_NullProductId_ThrowsException() {
    User user = createUser();

    assertThrows(IllegalArgumentException.class,
        () -> orderService.placeOrder(user, List.of(new OrderItemRequest(null, 1))));
  }

  @Test
  void testPlaceOrder_NullQuantity_ThrowsException() {
    User user = createUser();

    assertThrows(IllegalArgumentException.class,
        () -> orderService.placeOrder(user, List.of(new OrderItemRequest(1L, null))));
  }

  @Test
  void testPlaceOrder_MultipleItems_BatchFetchesOnce() {
    User user = createUser();
    Product product1 = new Product("Nasi Goreng", "Enak", BigDecimal.valueOf(15000));
    Product product2 = new Product("Mie Ayam", "Enak", BigDecimal.valueOf(17000));
    ReflectionTestUtils.setField(product1, "id", 1L);
    ReflectionTestUtils.setField(product2, "id", 2L);
    doReturn(List.of(product1, product2)).when(productRepository).findAllById((Iterable<Long>) any());
    when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

    Order order = orderService.placeOrder(
        user,
        List.of(
            new OrderItemRequest(1L, 2),
            new OrderItemRequest(2L, 1)));

    assertNotNull(order);
    assertEquals(2, order.getItems().size());
    assertEquals(BigDecimal.valueOf(47000), order.grandTotal());
    verify(productRepository, times(1)).findAllById((Iterable<Long>) any());
    verify(orderRepository, times(1)).save(any(Order.class));
  }

  @Test
  void testGetOrders() {
    User user = createUser();
    Order order1 = new Order(user);
    Order order2 = new Order(user);
    Pageable pageable = PageRequest.of(0, 2);
    when(orderRepository.findByUser(eq(user), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of(order1, order2), pageable, 5));

    PageResponse<Order> response = orderService.getOrders(user, 0, 2);

    assertNotNull(response);
    assertEquals(0, response.getPageNo());
    assertEquals(2, response.getPageSize());
    assertEquals(5L, response.getTotalElements());
    assertEquals(3, response.getTotalPages());
    assertEquals(false, response.isLast());
    assertEquals(2, response.getContent().size());
    verify(orderRepository, times(1)).findByUser(eq(user), eq(pageable));
  }

  @Test
  void testGetAllOrdersForAdmin() {
    User user = createUser();
    Order order1 = new Order(user);
    Pageable pageable = PageRequest.of(1, 1);
    when(orderRepository.findAll(eq(pageable)))
        .thenReturn(new PageImpl<>(List.of(order1), pageable, 2));

    PageResponse<Order> response = orderService.getOrdersForAdmin(1, 1);

    assertNotNull(response);
    assertEquals(1, response.getPageNo());
    assertEquals(1, response.getPageSize());
    assertEquals(2L, response.getTotalElements());
    assertEquals(2, response.getTotalPages());
    assertEquals(true, response.isLast());
    assertEquals(1, response.getContent().size());
    verify(orderRepository, times(1)).findAll(eq(pageable));
  }

  @Test
  void testUpdateOrderStatus_Success() {
    User user = createUser();
    Order order = new Order(user);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.save(order)).thenReturn(order);

    Order updated = orderService.updateOrderStatus(1L, OrderStatus.COMPLETED);

    assertNotNull(updated);
    assertEquals(OrderStatus.COMPLETED, updated.getStatus());
  }

  @Test
  void testUpdateOrderStatus_NullStatus_ThrowsException() {
    User user = createUser();
    Order order = new Order(user);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    assertThrows(IllegalArgumentException.class,
        () -> orderService.updateOrderStatus(1L, null));
  }

  @Test
  void testUpdateOrderStatus_NotFound_ThrowsException() {
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class,
        () -> orderService.updateOrderStatus(1L, OrderStatus.COMPLETED));
  }
}
