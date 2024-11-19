package com.bookstore.mapper;

import com.bookstore.dto.OrderItemDto;
import com.bookstore.entity.OrderItem;
import java.util.List;
import java.util.Set;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Named("toDto")
    OrderItemDto toDto(OrderItem orderItem);

    @IterableMapping(qualifiedByName = "toDto")
    List<OrderItemDto> toDtoList(Set<OrderItem> orderItems);
}
