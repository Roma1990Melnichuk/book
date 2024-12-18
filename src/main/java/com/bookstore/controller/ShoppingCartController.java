package com.bookstore.controller;

import com.bookstore.dto.ShoppingCartDto;
import com.bookstore.dto.UpdateCartItemDto;
import com.bookstore.entity.User;
import com.bookstore.response.ResponseHandler;
import com.bookstore.response.SuccessResponse;
import com.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart Management", description = "Endpoints for managing shopping carts")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "Get user's shopping cart",
            description = "Retrieve the shopping cart of the authenticated user")
    public SuccessResponse<ShoppingCartDto> getShoppingCart(@AuthenticationPrincipal User user) {
        return ResponseHandler.getSuccessResponse(
                shoppingCartService.getByUserId(user.getId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a book to the shopping cart",
            description = "Add a book to the user's shopping cart")
    public ShoppingCartDto addBookToShoppingCart(@RequestBody @Valid
                    UpdateCartItemDto request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addBookToShoppingCart(request, user);
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update the quantity of an item in the shopping cart",
            description = "Update the quantity of a specific item in the user's shopping cart")
    public SuccessResponse<ShoppingCartDto> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemDto requestDto,
            @AuthenticationPrincipal User user) {
        shoppingCartService.updateCartItemQuantity(cartItemId, requestDto, user);
        return ResponseHandler.getSuccessResponse(
                shoppingCartService.getByUserId(user.getId()));
    }

    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete item from shopping cart",
            description = "Remove a specific item from the user's shopping cart")
    public void deleteBookFromCartById(@PathVariable Long cartItemId,
                                       @AuthenticationPrincipal User user) {
        shoppingCartService.deleteCartItem(cartItemId, user);
    }
}
