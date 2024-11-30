package bookstore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.OnlineBookstoreApplication;
import com.bookstore.dto.CartItemResponseDto;
import com.bookstore.dto.ShoppingCartDto;
import com.bookstore.dto.UpdateCartItemDto;
import com.bookstore.entity.User;
import com.bookstore.service.ShoppingCartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = OnlineBookstoreApplication.class)
@AutoConfigureMockMvc
class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
    }

    @Test
    @DisplayName("Get shopping cart")
    void getShoppingCart_ReturnsUserShoppingCart() throws Exception {
        ShoppingCartDto mockShoppingCartDto = createMockShoppingCartDto();
        mockShoppingCartDto.setId(1L);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
        when(shoppingCartService.getByUserId(1L)).thenReturn(mockShoppingCartDto);

        mockMvc.perform(get("/cart").with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.cartItems.length()").value(2));
    }

    @Test
    @DisplayName("Add book to shopping cart")
    void addBookToShoppingCart_ReturnsUpdatedShoppingCart() throws Exception {
        UpdateCartItemDto request = new UpdateCartItemDto();
        request.setBookId(123L);
        request.setQuantity(1);
        ShoppingCartDto mockShoppingCartDto = createMockShoppingCartDto();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));

        when(shoppingCartService.addBookToShoppingCart(any(UpdateCartItemDto.class), eq(user)))
                .thenReturn(mockShoppingCartDto);

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(authentication(authentication)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cartItems.length()").value(2));

        verify(shoppingCartService).addBookToShoppingCart(any(UpdateCartItemDto.class), eq(user));
    }

    @Test
    @DisplayName("Update the quantity of an item in the cart")
    void updateCartItemQuantity_ReturnsUpdatedShoppingCart() throws Exception {
        UpdateCartItemDto requestDto = new UpdateCartItemDto();
        requestDto.setBookId(101L);
        requestDto.setQuantity(2);

        Set<CartItemResponseDto> cartItems = new HashSet<>();
        CartItemResponseDto cartItemResponseDto = new CartItemResponseDto();
        cartItemResponseDto.setQuantity(2);
        cartItemResponseDto.setBookId(101L);
        cartItems.add(cartItemResponseDto);

        ShoppingCartDto mockShoppingCartDto = new ShoppingCartDto();
        mockShoppingCartDto.setId(1L);
        mockShoppingCartDto.setCartItems(cartItems);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));

        when(shoppingCartService.updateCartItemQuantity(anyLong(), eq(requestDto), eq(user)))
                .thenReturn(mockShoppingCartDto);
        when(shoppingCartService.getByUserId(eq(user.getId()))).thenReturn(mockShoppingCartDto);

        mockMvc.perform(put("/cart/items/{cartItemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.cartItems.length()").value(1))
                .andExpect(jsonPath("$.data.cartItems[0].bookId").value(101))
                .andExpect(jsonPath("$.data.cartItems[0].quantity").value(2));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Remove book from shopping cart (invalid data)")
    void removeBookFromCart_InvalidInput_ThrowsException() throws Exception {
        Long cartItemId = 999L;

        mockMvc.perform(delete("/shoppingCarts/cart-items/{cartItemId}", cartItemId))
                .andExpect(status().isNotFound());
    }

    private ShoppingCartDto createMockShoppingCartDto() {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(1L);

        CartItemResponseDto item1 = new CartItemResponseDto();
        item1.setBookId(101L);
        item1.setBookTitle("Book 1");
        item1.setQuantity(2);

        CartItemResponseDto item2 = new CartItemResponseDto();
        item2.setBookId(102L);
        item2.setBookTitle("Book 2");
        item2.setQuantity(3);

        Set<CartItemResponseDto> cartItems = new HashSet<>();
        cartItems.add(item1);
        cartItems.add(item2);
        shoppingCartDto.setCartItems(cartItems);

        return shoppingCartDto;
    }
}
