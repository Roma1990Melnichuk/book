package com.bookstore.mapper;

import com.bookstore.dto.CartItemRequestDto;
import com.bookstore.dto.CartItemResponseDto;
import com.bookstore.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemResponseDto toDto(CartItem cartItem);

    @Mapping(source = "bookId", target = "book.id")
    CartItem toModel(CartItemRequestDto dto);
}
