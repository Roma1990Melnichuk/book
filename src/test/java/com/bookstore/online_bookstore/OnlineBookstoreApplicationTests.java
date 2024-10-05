//package com.bookstore.online_bookstore;
//
//import com.bookstore.dto.BookDto;
//import com.bookstore.dto.CreateBookRequestDto;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import entity.Book;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import repository.BookRepository;
//
//import java.math.BigDecimal;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class OnlineBookstoreApplicationTests {
//	@Autowired
//	private MockMvc mockMvc;
//	@Autowired
//	private BookRepository bookRepository;
//
//	@BeforeEach
//	public void setUp() {
//		// Создаем объект книги
//		Book book = new Book();
//		book.setId(1L);
//		book.setTitle("Тестовая книга");
//		book.setAuthor("Автор тестовой книги");
//		book.setIsbn("ISBN-1234567890");
//		book.setPrice(BigDecimal.valueOf(19.99));
//
//		// Сохраняем книгу в базе данных
//		bookRepository.save(book);
//	}
//
//	@Test
//	public void testGetAllBooks() throws Exception {
//		mockMvc.perform(get("/api/books"))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
//	}
//
//	@Test
//	public void testGetBookById() throws Exception {
//		mockMvc.perform(get("/api/books/{id}", 1))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
//	}
//
//	@Test
//	public void testCreateBook() throws Exception {
//		CreateBookRequestDto newBook = new CreateBookRequestDto();
//		newBook.setTitle("New Book Title");
//		newBook.setAuthor("Author Name");
//		newBook.setPrice(BigDecimal.valueOf(29.99));
//		newBook.setIsbn("978-3-16-148410-0");
//		newBook.setDescription("Description of the new book.");
//
//		mockMvc.perform(post("/api/books")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(new ObjectMapper().writeValueAsString(newBook)))
//				.andExpect(status().isCreated())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
//	}
//}
