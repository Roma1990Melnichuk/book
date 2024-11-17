package com.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateCartItemDto {
    @NotNull(message = "Book ID cannot be null")
    @Positive
    private Long bookId;

    @Positive
    private int quantity;
}
