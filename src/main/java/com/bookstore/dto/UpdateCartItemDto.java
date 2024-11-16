package com.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemDto {
    @NotNull(message = "Book ID cannot be null")
    private Long bookId;

    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;
}
