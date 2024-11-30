package bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.BookDtoWithoutCategoryIds;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.entity.Book;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.service.BookServiceImpl;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_NewBookProvided_BookSavedSuccess() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        Book book = new Book();
        BookDto expectedDto = new BookDto();
        when(bookMapper.toBook(requestDto)).thenReturn(book);
        when(bookMapper.toBookDto(book)).thenReturn(expectedDto);
        when(bookRepository.save(book)).thenReturn(book);

        BookDto savedBook = bookService.save(requestDto);

        assertEquals(expectedDto, savedBook);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void findById_ExistingIdProvided_BookFoundSuccess() {
        long bookId = 1L;
        Book book = new Book();
        BookDto expectedDto = new BookDto();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toBookDto(book)).thenReturn(expectedDto);

        BookDto foundBook = bookService.findById(bookId);

        assertEquals(expectedDto, foundBook);
    }

    @Test
    void findById_NonExistingIdProvided_EntityNotFoundExceptionThrown() {
        long nonExistingId = 999L;
        when(bookRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.findById(nonExistingId));
    }

    @Test
    void findAll_ValidPageableProvided_AllBooksRetrievedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        bookService.findAll(pageable);

        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void update_ExistingBookAndIdProvided_BookUpdatedSuccess() {
        long bookId = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        Book bookToUpdate = new Book();
        Book updatedBook = new Book();
        BookDto expectedDto = new BookDto();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookToUpdate));
        when(bookMapper.toBookDto(updatedBook)).thenReturn(expectedDto);
        when(bookRepository.save(bookToUpdate)).thenReturn(updatedBook);

        BookDto updatedDto = bookService.update(bookId, requestDto);

        assertEquals(expectedDto, updatedDto);
        verify(bookRepository, times(1)).save(bookToUpdate);
    }

    @Test
    void delete_ExistingIdProvided_BookDeletedSuccess() {
        long bookId = 1L;

        bookService.delete(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void getBooksByCategoryId_ExistingCategoryIdProvided_BooksFoundSuccess() {
        long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();
        when(bookRepository.findByCategoriesId(categoryId, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(new Book())));

        Page<BookDtoWithoutCategoryIds> result = bookService.getBooksByCategoryId(
                categoryId, pageable);

        assertNotNull(result);
        verify(bookRepository, times(1)).findByCategoriesId(categoryId, pageable);
    }
}
