package com.bookstore.repository;

import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "orderItems")
    Page<Order> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findById(Long id);
}
