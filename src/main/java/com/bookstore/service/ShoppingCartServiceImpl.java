package com.bookstore.service;

import java.util.List;
import java.util.stream.Collectors;

import com.bookstore.dto.CartItemRequestDto;
import com.bookstore.dto.ShoppingCartDto;
import com.bookstore.dto.ShoppingCartRequestDto;
import com.bookstore.entity.Book;
import com.bookstore.entity.CartItemKey;
import com.bookstore.entity.ShoppingCart;
import com.bookstore.entity.User;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.ShoppingCartMapper;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final LocaleService localeService;
    private final BookService bookService;

    @Override
    public void createFor(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setId(user.getId());
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto save(ShoppingCartRequestDto dto, User user) {
        List<Long> newBookIds = dto.getCartItems().stream()
                .map(CartItemRequestDto::getBookId)
                .toList();
        bookService.validateBooksExistence(newBookIds);

        ShoppingCart shoppingCart = shoppingCartMapper.toModel(dto);
        shoppingCart.setUser(user);
        shoppingCart.setId(user.getId());

        shoppingCart.setCartItems(shoppingCart.getCartItems().stream()
                .peek(cartItem -> cartItem.setShoppingCart(shoppingCart))
                .peek(cartItem -> cartItem.setId(
                        new CartItemKey(shoppingCart.getId(), cartItem.getBook().getId())
                ))
                .collect(Collectors.toSet())
        );

        ShoppingCart savedShoppingCart = shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(savedShoppingCart);
    }

    @Override
    public ShoppingCartDto getByUserId(Long id) {
        ShoppingCart shoppingCart = shoppingCartRepository.getByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        localeService.getMessage("exception.notfound.shoppingcart") + id
                ));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteCartItem(Long bookId, Long userId) {
        ShoppingCart shoppingCart = new ShoppingCart(userId);
        Book book = new Book(bookId);
        CartItemKey id = new CartItemKey(shoppingCart.getId(), book.getId());

        if (!cartItemRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    localeService.getMessage("exception.notfound.cartitem") + bookId
            );
        }
        cartItemRepository.deleteById(id);
    }
}
