package com.bookstore.mapper;

import com.bookstore.dto.OrderDto;
import com.bookstore.dto.OrderItemDto;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    List<OrderDto> toDtoList(List<Order> orders);

    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order orders);

    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto mapOrderItem(OrderItem orderItem);
}
