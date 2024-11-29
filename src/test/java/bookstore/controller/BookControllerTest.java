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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

import javax.sql.DataSource;

@SpringBootTest(classes = OnlineBookstoreApplication.class)
class BookControllerTest {

    private static MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    private BookService bookService;

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
        MvcResult result = mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "5")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andReturn();

        assertNotNull(result.getResponse().getContentAsString());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get book by ID")
    void getBookById_ReturnsBookById_ExpectedSuccess() throws Exception {
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
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new book")
    void createBook_CreatesNewBook_ExpectedSuccess() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("John Doe");
        requestDto.setTitle("New Book Title");
        requestDto.setDescription("Book Description");
        requestDto.setIsbn("4" + System.currentTimeMillis());
        requestDto.setCoverImage("http://example.com/cover3.jpg");
        requestDto.setPrice(BigDecimal.valueOf(15));
        Category category = new Category();
        category.setId(1L);

        Set<Category> categories = new HashSet<>();
        categories.add(category);

        requestDto.setCategories(categories);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(requestDto, actual, "id");
    }


    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update an existing book")
    void updateBook_UpdatesBook_ExpectedSuccess() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("Updated1 Author");
        requestDto.setTitle("Updated Title");
        requestDto.setDescription("Updated Description");
        requestDto.setIsbn("942081");
        requestDto.setPrice(BigDecimal.valueOf(15.99));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        put("/books/{id}", 1L)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Updated1 Author"))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.isbn").value("942081"))
                .andExpect(jsonPath("$.price").value(15.99));
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
