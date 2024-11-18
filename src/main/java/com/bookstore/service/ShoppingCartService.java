package com.bookstore.service;

import com.bookstore.dto.ShoppingCartDto;
import com.bookstore.dto.UpdateCartItemDto;
import com.bookstore.entity.User;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto addBookToShoppingCart(UpdateCartItemDto itemDto, User user);

    ShoppingCartDto getByUserId(Long id);

    ShoppingCartDto updateCartItemQuantity(Long cartItemId, UpdateCartItemDto request, User user);

    void deleteCartItem(Long cartItemI, User userId);
}
