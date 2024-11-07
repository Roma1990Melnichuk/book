package com.bookstore.repository;

import com.bookstore.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<Role> findByName(Role.RoleName name);
}
