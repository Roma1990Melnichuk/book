package com.bookstore.service;

import com.bookstore.dto.ShoppingCartDto;
import com.bookstore.dto.UpdateCartItemDto;
import com.bookstore.entity.Book;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.ShoppingCart;
import com.bookstore.entity.User;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.ShoppingCartMapper;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto addBookToShoppingCart(UpdateCartItemDto itemDto, User user) {
        if (itemDto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserAndIsDeletedFalse(user)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found"));

        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(itemDto.getBookId()))
                .findFirst()
                .map(existingItem -> updateExistingCartItem(existingItem, itemDto.getQuantity()))
                .orElseGet(() -> {
                    Book book = bookRepository.findById(itemDto.getBookId())
                            .orElseThrow(() -> new EntityNotFoundException("Book not found"));
                    return createNewCartItem(book, itemDto.getQuantity(), shoppingCart);
                });

        if (cartItem != null) {
            shoppingCart.getCartItems().add(cartItem);
        }

        shoppingCartRepository.save(shoppingCart);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto getByUserId(Long id) {
        ShoppingCart shoppingCart = shoppingCartRepository.getByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart for user with ID " + id + " not found.")
                );
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "CartItem with ID " + cartItemId + " not found"));

        ShoppingCart shoppingCart = cartItem.getShoppingCart();
        if (shoppingCart.isDeleted()) {
            throw new IllegalStateException("Cannot delete CartItem from a deleted ShoppingCart");
        }

        cartItemRepository.delete(cartItem);
    }

    @Override
    public ShoppingCartDto updateCartItemQuantity(Long cartItemId,
                                                  UpdateCartItemDto updateCartItemDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "CartItem with ID " + cartItemId + " not found"));

        ShoppingCart shoppingCart = cartItem.getShoppingCart();
        if (shoppingCart.isDeleted()) {
            throw new IllegalStateException("Cannot update CartItem in a deleted ShoppingCart");
        }

        cartItem.setQuantity(updateCartItemDto.getQuantity());
        cartItemRepository.save(cartItem);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    private CartItem createNewCartItem(Book book, int quantity, ShoppingCart shoppingCart) {
        CartItem newCartItem = new CartItem();
        newCartItem.setBook(book);
        newCartItem.setQuantity(quantity);
        newCartItem.setShoppingCart(shoppingCart);
        return newCartItem;
    }

    private CartItem updateExistingCartItem(CartItem cartItem, int quantity) {
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        return cartItem;
    }
}
