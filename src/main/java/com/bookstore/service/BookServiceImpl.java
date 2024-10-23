package com.bookstore.service;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.entity.Book;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
    public List<BookDto> findAll() {
        return bookRepository.findAllByDeletedFalse()
                .stream()
                .map(bookMapper::toBookDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Book with id " + id + " not found"));
        return bookMapper.toBookDto(book);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto bookRequestDto) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Book with id " + id + " not found"));

        Optional.ofNullable(bookRequestDto.getTitle())
                .filter(title -> !title.equals(existingBook.getTitle()))
                .ifPresent(existingBook::setTitle);

        Optional.ofNullable(bookRequestDto.getAuthor())
                .filter(author -> !author.equals(existingBook.getAuthor()))
                .ifPresent(existingBook::setAuthor);

        Optional.ofNullable(bookRequestDto.getIsbn())
                .filter(isbn -> !isbn.equals(existingBook.getIsbn()))
                .ifPresent(existingBook::setIsbn);

        Optional.ofNullable(bookRequestDto.getPrice())
                .filter(price -> price.compareTo(existingBook.getPrice()) != 0)
                .ifPresent(existingBook::setPrice);

        Optional.ofNullable(bookRequestDto.getDescription())
                .filter(description -> !description.equals(existingBook.getDescription()))
                .ifPresent(existingBook::setDescription);

        Optional.ofNullable(bookRequestDto.getCoverImage())
                .filter(coverImage -> !coverImage.equals(existingBook.getCoverImage()))
                .ifPresent(existingBook::setCoverImage);


        return bookMapper.toBookDto(bookRepository.save(existingBook));
    }

    @Override
    public void delete(Long id) {
        Book existingBook = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Book with id " + id + " not found"));
        existingBook.setDeleted(true);
        bookRepository.save(existingBook);
    }
}
