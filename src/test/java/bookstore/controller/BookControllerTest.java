package bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.OnlineBookstoreApplication;
import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.entity.Category;
import com.bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = OnlineBookstoreApplication.class)
class BookControllerTest {

    private static MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-three-default-books.sql")
            );
        }
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books")
    void getAllBooks_ReturnsAllBooks_ExpectedSuccess() throws Exception {
        BookDto book1 = new BookDto();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");

        BookDto book2 = new BookDto();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");

        List<BookDto> books = Arrays.asList(book1, book2);
        Page<BookDto> page = new PageImpl<>(books);

        Pageable pageable = PageRequest.of(0, 5);

        Mockito.when(bookService.findAll(pageable)).thenReturn(page);

        MvcResult result = mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "5")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andReturn();

        assertNotNull(result.getResponse().getContentAsString());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get book by ID")
    void getBookById_ReturnsBookById_ExpectedSuccess() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");
        bookDto.setAuthor("Test Author");

        Mockito.when(bookService.findById(1L)).thenReturn(bookDto);

        MvcResult result = mockMvc.perform(
                        get("/books/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actualBook = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto.class);
        assertNotNull(actualBook);
        assertEquals(1L, actualBook.getId());
        assertEquals("Test Book", actualBook.getTitle());
        assertEquals("Test Author", actualBook.getAuthor());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new book")
    void createBook_CreatesNewBook_ExpectedSuccess() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("John Doe");
        requestDto.setTitle("New Book Title");
        requestDto.setDescription("Book Description");
        requestDto.setIsbn("4602081" + System.currentTimeMillis());
        requestDto.setCoverImage("http://example.com/cover3.jpg");
        requestDto.setPrice(BigDecimal.valueOf(15));

        Category category = new Category();
        category.setId(1L);

        Set<Category> categories = new HashSet<>();
        categories.add(category);
        requestDto.setCategories(categories);

        BookDto mockBook = new BookDto();
        mockBook.setId(1L);
        mockBook.setTitle("New Book Title");
        mockBook.setAuthor("John Doe");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        Mockito.when(bookService.save(
                Mockito.any(CreateBookRequestDto.class))).thenReturn(mockBook);

        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent);

        BookDto createdBook = objectMapper.readValue(responseContent, BookDto.class);
        assertNotNull(createdBook);
        assertEquals(mockBook.getTitle(), createdBook.getTitle());
        assertEquals(mockBook.getAuthor(), createdBook.getAuthor());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update an existing book")
    void updateBook_UpdatesBook_ExpectedSuccess() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("Updated Author");
        requestDto.setTitle("Updated Title");
        requestDto.setDescription("Updated Description");
        requestDto.setIsbn("942081");
        requestDto.setPrice(BigDecimal.valueOf(12.99));

        BookDto mockUpdatedBookDto = new BookDto();
        mockUpdatedBookDto.setTitle("Updated Title");
        mockUpdatedBookDto.setAuthor("Updated Author");
        mockUpdatedBookDto.setDescription("Updated Description");
        mockUpdatedBookDto.setIsbn("942081");
        mockUpdatedBookDto.setPrice(BigDecimal.valueOf(12.99));

        Mockito.when(bookService.update(Mockito.anyLong(), Mockito.any(CreateBookRequestDto.class)))
                .thenReturn(mockUpdatedBookDto);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        put("/books/{id}", 1L)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent);

        BookDto updatedBook = objectMapper.readValue(responseContent, BookDto.class);
        assertNotNull(updatedBook);
        assertEquals(requestDto.getTitle(), updatedBook.getTitle());
        assertEquals(requestDto.getAuthor(), updatedBook.getAuthor());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete a book")
    void deleteBook_DeletesBook_ExpectedSuccess() throws Exception {
        mockMvc.perform(
                        delete("/books/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM books");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
