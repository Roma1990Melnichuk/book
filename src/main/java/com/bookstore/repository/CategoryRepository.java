package com.bookstore.repository;

import com.bookstore.entity.Category;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);


    Set<Category> findByIdIn(List<Long> ids);
}
