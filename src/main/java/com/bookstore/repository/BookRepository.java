package com.bookstore.repository;

import com.bookstore.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByIsDeletedFalse();

    Optional<Book> findByIdAndIsDeletedFalse(Long id);
}
