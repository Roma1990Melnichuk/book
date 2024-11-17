package com.bookstore.service;

import com.bookstore.dto.CartItemRequestDto;
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
import jakarta.transaction.Transactional;
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
    public ShoppingCartDto save(UpdateCartItemDto itemDto, User user) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserAndIsDeletedFalse(user)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user with ID " + user.getId() + " or it is deleted"));

        Book book = bookRepository.findById(itemDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with ID " + itemDto.getBookId() + " not found"));

        CartItem existingCartItem = shoppingCart.getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().equals(book))
                .findFirst()
                .orElse(null);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + itemDto.getQuantity());
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem newCartItem = createNewCartItem(book, itemDto.getQuantity(), shoppingCart);
            shoppingCart.getCartItems().add(newCartItem);
            cartItemRepository.save(newCartItem);
        }

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
    public ShoppingCartDto updateCartItemQuantity(Long cartItemId, UpdateCartItemDto updateCartItemDto) {
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
}
