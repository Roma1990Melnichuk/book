package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    @Schema(description = "Title of the book", example = " Clean code")
    @NotBlank
    private String title;
    @Schema(description = "Author of the book", example = "Robert Martin")
    @NotBlank
    private String author;
    @Schema(description = "ISBN number of the book", example = "9781234567897")
    private String isbn;
    @Schema(description = "Price of the book", example = "10.15")
    @NotNull
    @Min(0)
    private BigDecimal price;
    @Schema(description = "Description of the book",
            example = "Clean Code is divided into three parts...")
    private String description;
    @Schema(description = "Cover image URL of the book", example = "http://example.com/cover.jpg")
    private String coverImage;
}
