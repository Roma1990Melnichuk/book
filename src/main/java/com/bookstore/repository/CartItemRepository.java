package com.bookstore.repository;

import com.bookstore.entity.CartItem;
import com.bookstore.entity.CartItemKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemKey> {
}
