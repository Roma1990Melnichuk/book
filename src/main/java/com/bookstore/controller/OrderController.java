package com.bookstore.controller;

import com.bookstore.dto.OrderDto;
import com.bookstore.dto.OrderItemDto;
import com.bookstore.dto.OrderRequest;
import com.bookstore.dto.UpdateOrderStatusRequest;
import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import com.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OrderController management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Place an order", description = "Endpoint for placing an order")
    @PostMapping
    public OrderDto placeOrder(@RequestBody @Valid OrderRequest request,
                               Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.createOrder(request, user);
    }

    @Operation(summary = "Get all orders",
            description = "Endpoint for retrieving user's order history")
    @GetMapping
    public List<OrderDto> getOrderHistory(Pageable pageable, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.findAll(user, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update order status",
            description = "Endpoint for updating the status of an order")
    @PatchMapping("/{id}")
    public OrderDto updateOrderStatus(@PathVariable Long id,
                                      @RequestBody @Valid UpdateOrderStatusRequest request) {
        return orderService.updateOrderStatus(id, Order.Status.valueOf(request.getStatus()));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Retrieve all OrderItems for a specific order",
            description = "Endpoint for retrieving all OrderItems for a specific order")
    @GetMapping("/{orderId}/items")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderItemDto> getAllOrderItemsForOrder(@PathVariable Long orderId) {
        return orderService.getAllOrderItems(orderId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Retrieve a specific Ord"
            + " @ResponseStatus(HttpStatus.OK)erItem within an order",
            description = "Endpoint for retrieving a specific OrderItem within an order")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItemForOrder(@PathVariable Long orderId,
                                             @PathVariable Long itemId) {
        return orderService.getOrderItem(orderId, itemId);
    }
}
