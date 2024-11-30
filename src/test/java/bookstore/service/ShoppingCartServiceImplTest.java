package bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.bookstore.service.ShoppingCartServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ShoppingCartServiceImplTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private User user;
    private ShoppingCart shoppingCart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
    }

    @Test
    void createShoppingCart_ShouldCreateNewShoppingCart() {
        shoppingCartService.createShoppingCart(user);

        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }

    @Test
    void addBookToShoppingCart_ShouldAddBookToCart_WhenNewItem() {
        UpdateCartItemDto itemDto = new UpdateCartItemDto();
        itemDto.setBookId(1L);
        itemDto.setQuantity(1);

        Book book = new Book();
        book.setId(1L);

        when(bookRepository.findById(itemDto.getBookId())).thenReturn(Optional.of(book));
        when(shoppingCartRepository.findByUserAndIsDeletedFalse(user))
                .thenReturn(Optional.of(shoppingCart));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.addBookToShoppingCart(itemDto, user);

        assertNotNull(result);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }

    @Test
    void addBookToShoppingCart_ShouldUpdateQuantity_WhenItemExists() {
        UpdateCartItemDto itemDto = new UpdateCartItemDto();
        itemDto.setBookId(1L);
        itemDto.setQuantity(1);

        Book book = new Book();
        book.setId(1L);

        CartItem existingCartItem = new CartItem();
        existingCartItem.setBook(book);
        existingCartItem.setQuantity(1);

        shoppingCart.getCartItems().add(existingCartItem);

        when(bookRepository.findById(itemDto.getBookId())).thenReturn(Optional.of(book));
        when(shoppingCartRepository
                .findByUserAndIsDeletedFalse(user)).thenReturn(Optional.of(shoppingCart));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.addBookToShoppingCart(itemDto, user);

        assertEquals(2, existingCartItem.getQuantity());
        assertNotNull(result);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }

    @Test
    void getByUserId_ShouldReturnShoppingCartDto() {
        when(shoppingCartRepository
                .getByUserId(user.getId())).thenReturn(Optional.of(shoppingCart));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.getByUserId(user.getId());

        assertNotNull(result);
        verify(shoppingCartRepository, times(1)).getByUserId(user.getId());
    }

    @Test
    void getByUserId_ShouldThrowEntityNotFoundException_WhenShoppingCartNotFound() {
        when(shoppingCartRepository.getByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getByUserId(user.getId()));
    }

    @Test
    void deleteCartItem_ShouldThrowEntityNotFoundException_WhenCartItemNotFound() {
        when(cartItemRepository.findByIdAndShoppingCartId(1L, shoppingCart.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.deleteCartItem(1L, user));
    }

    @Test
    void updateCartItemQuantity_ShouldThrowEntityNotFoundException_WhenCartItemNotFound() {
        UpdateCartItemDto updateCartItemDto = new UpdateCartItemDto();
        updateCartItemDto.setQuantity(5);

        when(cartItemRepository.findByIdAndShoppingCartId(1L, shoppingCart.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItemQuantity(1L, updateCartItemDto, user));
    }
}
