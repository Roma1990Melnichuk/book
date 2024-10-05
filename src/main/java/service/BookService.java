package service;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import entity.Book;

import java.util.List;

public interface BookService {

    Book save(Book book);

    List<Book> findAll();

    Book findById(Long id);

    void deleteById(Long id);

    List<BookDto> getAllBooks();

    BookDto getBookById(Long id);

    BookDto createBook(CreateBookRequestDto bookDto);

}