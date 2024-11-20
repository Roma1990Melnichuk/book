package com.bookstore.service;

import com.bookstore.dto.OrderDto;
import com.bookstore.dto.OrderItemDto;
import com.bookstore.dto.OrderRequest;
import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(OrderRequest requests, User user);

    List<OrderDto> findAll(User user, Pageable pageable);

    OrderDto updateOrderStatus(Long id, Order.Status status);

    List<OrderItemDto> getAllOrderItems(Long orderId);

    OrderItemDto getOrderItem(Long orderId, Long itemId);
}
