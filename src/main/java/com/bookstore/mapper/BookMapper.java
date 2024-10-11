package com.bookstore.mapper;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toBookDto(Book book);

    default Book toBook(CreateBookRequestDto bookDto) {
        Book book = new Book();
        book.setId(bookDto.getId());
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setIsbn(bookDto.getIsbn());
        book.setPrice(bookDto.getPrice());
        book.setDescription(bookDto.getDescription());
        book.setСoverImage(bookDto.getCoverImage());
        return book;
    }
}
