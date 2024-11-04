package com.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Override
    public String getAuthority() {
        return "ROLE_" + name.name();
    }

    public enum RoleName {
        ROLE_USER,
        ROLE_ADMIN
    }
}
