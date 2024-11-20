package com.bookstore.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OrderRequest {
    @NotEmpty(message = "Shipping address is required")
    private String shippingAddress;
}

