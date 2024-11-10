package com.bookstore.mapper;

import com.bookstore.config.MapperConfig;
import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.CategoryRequestDto;
import com.bookstore.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "deleted", ignore = true)
    })
    Category toModel(CategoryRequestDto dto);
}
