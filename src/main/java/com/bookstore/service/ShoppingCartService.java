package com.bookstore.service;

import com.bookstore.dto.CartItemRequestDto;
import com.bookstore.dto.ShoppingCartDto;
import com.bookstore.dto.UpdateCartItemDto;
import com.bookstore.entity.User;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto save(UpdateCartItemDto itemDto, User user);

    ShoppingCartDto getByUserId(Long id);

    ShoppingCartDto updateCartItemQuantity(Long cartItemId, UpdateCartItemDto request);

    void deleteCartItem(Long cartItemId);
}
