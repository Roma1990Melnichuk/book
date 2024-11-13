package com.bookstore.mapper;

import com.bookstore.dto.ShoppingCartDto;
import com.bookstore.dto.ShoppingCartRequestDto;
import com.bookstore.entity.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {
    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
    })
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Mappings({
            @Mapping(source = "cartItems", target = "cartItems"),
    })
    ShoppingCart toModel(ShoppingCartRequestDto dto);
}
