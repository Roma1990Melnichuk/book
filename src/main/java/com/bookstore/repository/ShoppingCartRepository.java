package com.bookstore.repository;

import java.util.Optional;
import com.bookstore.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("FROM ShoppingCart sc "
            + "LEFT JOIN FETCH sc.cartItems ci "
            + "JOIN FETCH ci.book "
            + "WHERE sc.user.id = :userId")
    Optional<ShoppingCart> getByUserId(Long userId);
}
