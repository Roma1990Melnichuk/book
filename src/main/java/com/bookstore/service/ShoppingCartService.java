package com.bookstore.service;

import com.bookstore.dto.ShoppingCartDto;
import com.bookstore.dto.ShoppingCartRequestDto;
import com.bookstore.entity.User;

public interface ShoppingCartService {
    void createFor(User user);

    ShoppingCartDto save(ShoppingCartRequestDto dto, User user);

    ShoppingCartDto getByUserId(Long id);

    void deleteCartItem(Long bookId, Long userId);
}
