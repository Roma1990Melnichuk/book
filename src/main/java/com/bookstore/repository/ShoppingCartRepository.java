package com.bookstore.repository;

import com.bookstore.entity.ShoppingCart;
import com.bookstore.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    @EntityGraph(attributePaths = {"cartItems", "cartItems.book"})
    Optional<ShoppingCart> getByUserId(Long userId);

    Optional<ShoppingCart> findByUser(User currentUser);
}
