package com.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemDto {
    @NotNull
    private Long bookId;

    @NotNull
    private Integer quantity;
}
