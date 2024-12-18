package com.bookstore.controller;

import com.bookstore.dto.BookDtoWithoutCategoryIds;
import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.CategoryRequestDto;
import com.bookstore.response.ErrorResponse;
import com.bookstore.response.ResponseHandler;
import com.bookstore.response.SuccessResponse;
import com.bookstore.service.BookService;
import com.bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category Management", description = "Endpoints for managing categories")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @Operation(summary = "Create a new category", description = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409",
                    description = "Category with such name already exists",
                    content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
    })
    public SuccessResponse<CategoryDto> createCategory(
            @Valid @RequestBody CategoryRequestDto categoryDto) {
        return ResponseHandler.getSuccessResponse(
                categoryService.save(categoryDto),
                HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Get all categories")
    public SuccessResponse<Page<CategoryDto>> getAll(Pageable pageable) {
        return ResponseHandler.getSuccessResponse(
                categoryService.findAll(pageable)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id", description = "Get category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", content =
                    { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
    })
    public SuccessResponse<CategoryDto> getCategoryById(@PathVariable Long id) {
        return ResponseHandler.getSuccessResponse(
                categoryService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing category", description = "Update an existing category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409",
                    description = "Category with such name already exists "
                            + "(possible if new name differs from old one)",
                    content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
    })
    public SuccessResponse<CategoryDto> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryRequestDto categoryDto) {
        return ResponseHandler.getSuccessResponse(
                categoryService.update(id, categoryDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category by id", description = "Delete category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", content =
                    { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
    })
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Get all books by category id",
            description = "Get all books in a specific category with pagination support")
    public Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(
            @PathVariable Long id, Pageable pageable) {
        return bookService.getBooksByCategoryId(id, pageable);
    }
}
