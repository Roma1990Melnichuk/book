package com.bookstore.service;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.BookDtoWithoutCategoryIds;
import com.bookstore.dto.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    Page<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id, Pageable pageable);

    BookDto update(Long id, CreateBookRequestDto bookRequestDto);

    void delete(Long id);
}
