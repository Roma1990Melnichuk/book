package com.bookstore.service;

import com.bookstore.dto.OrderDto;
import com.bookstore.dto.OrderItemDto;
import com.bookstore.dto.OrderRequest;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.ShoppingCart;
import com.bookstore.entity.User;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.OrderItemMapper;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    @Override
    public OrderDto orderReturn(OrderRequest request, User user) {
        Order order = new Order();
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(Order.Status.PENDING);
        BigDecimal total = BigDecimal.ZERO;
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found for user: "
                        + user.getUsername()));
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItems.add(orderItem);
            total = total.add(cartItem.getBook().getPrice().multiply(
                    BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotal(total);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(orderItems);
        order.setUser(user);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> findAll(User user, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByUser(user, pageable);
        List<Order> orders = ordersPage.getContent();
        return orderMapper.toDtoList(orders);
    }

    @Transactional
    @Override
    public OrderDto updateOrderStatus(Long id, Order.Status status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        order.setStatus(Order.Status.valueOf(status.name()));
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderItemDto> getAllOrderItems(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId));
        return orderItemMapper.toDtoList(order.getOrderItems());
    }

    @Override
    public OrderItemDto getOrderItem(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId));
        OrderItem orderItem = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order item not found with id: " + itemId));
        return orderItemMapper.toDto(orderItem);
    }
}
