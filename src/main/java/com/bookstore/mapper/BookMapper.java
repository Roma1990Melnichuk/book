package com.bookstore.mapper;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toBookDto(Book book);

    Book toBook(CreateBookRequestDto bookDto);
}
