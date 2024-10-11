package com.bookstore.service;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.bookstore.repository.BookRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto bookDto) {
        try {
            Book book = bookMapper.toBook(bookDto);
            Book savedBook = bookRepository.save(book);
            return bookMapper.toBookDto(savedBook);
        } catch (Exception e) {
            throw new RuntimeException("Error while saving book: " + e.getMessage());
        }
    }

    @Override
    public List<BookDto> findAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(book -> new BookDto(book.getId(),
                book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPrice(),
                book.getDescription(), book.getCoverImage())).collect(Collectors.toList());

    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        return bookMapper.toBookDto(book);
    }
}
