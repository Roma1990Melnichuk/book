package com.bookstore.mapper;

import com.bookstore.dto.CartItemRequestDto;
import com.bookstore.dto.CartItemResponseDto;
import com.bookstore.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mappings({
            @Mapping(source = "book.id", target = "bookId"),
            @Mapping(source = "book.title", target = "bookTitle")
    })
    CartItemResponseDto toDto(CartItem cartItem);

    @Mappings({
            @Mapping(source = "bookId", target = "book.id")
    })
    CartItem toModel(CartItemRequestDto dto);
}