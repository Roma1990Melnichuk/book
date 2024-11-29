package bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.OnlineBookstoreApplication;
import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.CategoryRequestDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.service.CategoryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OnlineBookstoreApplication.class)
@Sql(scripts = "/database/categories/add-three-default-categories.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CategoryControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    void setUp(@Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void cleanUpAfterEachTest() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/categories/remove-fantazy-category-to-categories-table.sql")
            );
        }
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all categories")
    void getAll_ReturnsAllCategories_ExpectedSuccess() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        JsonNode dataNode = rootNode.get("data");
        assertNotNull(dataNode);

        JsonNode contentNode = dataNode.get("content");
        assertNotNull(contentNode);

        CategoryDto[] categories = objectMapper.treeToValue(contentNode, CategoryDto[].class);
        Assertions.assertEquals(1, categories.length);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new category")
    void createCategory_CreatesNewCategory_ExpectedSuccess() throws Exception {
        String randomName = "Category_" + UUID.randomUUID().toString().substring(0, 8);
        String randomDescription = "Description_" + UUID.randomUUID().toString().substring(0, 8);

        CategoryRequestDto expected = new CategoryRequestDto();
        expected.setName(randomName);
        expected.setDescription(randomDescription);

        String jsonRequest = objectMapper.writeValueAsString(expected);

        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(responseContent);
        JsonNode dataNode = rootNode.get("data");

        assertNotNull(dataNode);

        CategoryDto actual = objectMapper.treeToValue(dataNode, CategoryDto.class);

        assertNotNull(actual);
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get category by ID")
    void getCategoryById_ReturnsCategoryById_ExpectedSuccess() throws Exception {
        Long categoryId = 1L;
        MvcResult result = mockMvc.perform(
                        get("/categories/{id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto category = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        assertNotNull(category);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update a category")
    void updateCategory_UpdatesCategoryDetails_ExpectedSuccess() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Updated Category Name");
        categoryDto.setDescription("Updated Category Description");

        String jsonRequest = objectMapper.writeValueAsString(categoryDto);

        MvcResult result = mockMvc.perform(
                        put("/categories/{id}", 1L)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        mockMvc.perform(
                        put("/categories/{id}", 1L)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Category Name"))
                .andExpect(jsonPath("$.data.description").value("Updated Category Description"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete a category")
    void deleteCategory_DeletesCategoryById_ExpectedSuccess() throws Exception {
        Long categoryId = 1L;

        MvcResult checkExistenceResult = mockMvc.perform(get("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String checkContent = checkExistenceResult.getResponse().getContentAsString();
        Assertions.assertFalse(checkContent.isBlank());

        mockMvc.perform(delete("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        try {
            MvcResult getResult = mockMvc.perform(get("/categories/{id}", categoryId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound()) // Ожидаем статус 404 (Not Found)
                    .andReturn();

            String content = getResult.getResponse().getContentAsString();
            Assertions.assertTrue(content.isBlank()); // Ожидаем, что тело будет пустым

        } catch (ServletException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof EntityNotFoundException) {
                Assertions.assertTrue(cause.getMessage().contains("Can't find a category by id"));
            } else {
                Assertions.fail("Unexpected exception: " + cause);
            }
        }
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get books by category ID")
    void getBooksByCategoryId_ReturnsBooksByCategoryId_ExpectedSuccess() throws Exception {
        Long categoryId = 1L;

        mockMvc.perform(get("/categories/{id}/books", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
