package com.bookstore.service;

import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.CategoryRequestDto;
import com.bookstore.entity.Category;
import com.bookstore.exception.EntityAlreadyExistsException;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.repository.CategoryRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final LocaleService localeService;

    @Override
    public Page<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toDto);
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = getModelById(id);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto save(CategoryRequestDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw getAlreadyExistsException();
        }
        Category category = categoryMapper.toModel(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public CategoryDto update(Long id, CategoryRequestDto categoryDto) {
        Category category = getModelById(id);
        String newName = categoryDto.getName();

        if (!Objects.equals(category.getName(), newName)
                && categoryRepository.existsByName(newName)) {
            throw getAlreadyExistsException();
        }

        categoryMapper.updateCategoryFromDto(categoryDto, category);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw getNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }

    private Category getModelById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> getNotFoundException(id));
    }

    private EntityAlreadyExistsException getAlreadyExistsException() {
        return new EntityAlreadyExistsException(
                localeService.getMessage("exception.exists.category")
        );
    }

    private EntityNotFoundException getNotFoundException(Long id) {
        return new EntityNotFoundException(
                localeService.getMessage("exception.notfound.category") + id
        );
    }
}
