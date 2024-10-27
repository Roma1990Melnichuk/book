package com.bookstore.service;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.entity.Book;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto bookDto) {
        Book book = bookMapper.toBook(bookDto);
        return bookMapper.toBookDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toBookDto);
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Book with id " + id + " not found"));
        return bookMapper.toBookDto(book);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto bookRequestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with id " + id + " not found"));
        bookMapper.updateBookFromDto(bookRequestDto, book);
        return bookMapper.toBookDto(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}
