package com.bookstore.mapper;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.BookDtoWithoutCategoryIds;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mappings({
            @Mapping(source = "categories", target = "categoryIds",
                    qualifiedByName = "categoryToId")
    })
    BookDto toBookDto(Book book);

    @Mappings({
            @Mapping(source = "categories", target = "categories"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })

    Book toBook(CreateBookRequestDto bookDto);

    void updateBookFromDto(CreateBookRequestDto bookRequestDto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @Named("categoryToId")
    default Long categoryToId(Category category) {
        return category.getId();
    }

    @Named("bookFromId")
    default Book bookFromId(Long id) {
        if (id == null) {
            return null;
        }
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
