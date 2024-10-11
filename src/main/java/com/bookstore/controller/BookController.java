package com.bookstore.controller;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/books")
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping()
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    public ResponseEntity createBook(@RequestBody CreateBookRequestDto requestDto) {
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto();
        createBookRequestDto.setAuthor(requestDto.getAuthor());
        createBookRequestDto.setDescription(requestDto.getDescription());
        createBookRequestDto.setIsbn(requestDto.getIsbn());
        createBookRequestDto.setPrice(requestDto.getPrice());
        createBookRequestDto.setTitle(requestDto.getTitle());
        createBookRequestDto.setCoverImage(requestDto.getCoverImage());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.save(createBookRequestDto));
    }
}
