package bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.CategoryRequestDto;
import com.bookstore.entity.Category;
import com.bookstore.exception.EntityAlreadyExistsException;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.service.CategoryServiceImpl;
import com.bookstore.service.LocaleService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private LocaleService localeService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ValidInput_ReturnsListOfCategoryDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Category category = new Category();
        CategoryDto categoryDto = new CategoryDto();
        when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(category)));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        Page<CategoryDto> result = categoryService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    void getById_ExistingCategoryId_ReturnsCategoryDto() {
        Long categoryId = 1L;
        Category category = new Category();
        CategoryDto categoryDto = new CategoryDto();
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getById(categoryId);

        assertEquals(categoryDto, result);
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void getById_NonExistingCategoryId_ThrowsEntityNotFoundException() {
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void save_ValidCategoryDto_ReturnsSavedCategoryDto() {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Test Category");
        Category category = new Category();
        Category savedCategory = new Category();
        CategoryDto categoryDto = new CategoryDto();

        when(categoryRepository.existsByName(categoryRequestDto.getName())).thenReturn(false);
        when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(categoryDto);

        CategoryDto result = categoryService.save(categoryRequestDto);

        assertEquals(categoryDto, result);
        verify(categoryRepository, times(1)).existsByName(categoryRequestDto.getName());
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toModel(categoryRequestDto);
        verify(categoryMapper, times(1)).toDto(savedCategory);
    }

    @Test
    void save_ExistingCategoryName_ThrowsEntityAlreadyExistsException() {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Existing Category");

        when(categoryRepository.existsByName(categoryRequestDto.getName())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> categoryService.save(categoryRequestDto));
        verify(categoryRepository, times(1)).existsByName(categoryRequestDto.getName());
    }

    @Test
    void update_ExistingCategoryIdAndValidCategoryDto_ReturnsUpdatedCategoryDto() {
        Long categoryId = 1L;

        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Updated Category");

        Category category = new Category();
        category.setId(categoryId);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryId);
        categoryDto.setName("Updated Category");

        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(category));
        when(categoryRepository.existsByName(categoryRequestDto.getName())).thenReturn(false);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto result = categoryService.update(categoryId, categoryRequestDto);

        assertNotNull(result);
        assertEquals(categoryDto, result);
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).existsByName(categoryRequestDto.getName());
        verify(categoryMapper).updateCategoryFromDto(categoryRequestDto, category);
        verify(categoryRepository).save(category);
    }

    @Test
    void update_ExistingCategoryName_ThrowsEntityAlreadyExistsException() {
        Long categoryId = 1L;
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Existing Category");

        Category category = new Category();
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(category));
        when(categoryRepository.existsByName(categoryRequestDto.getName())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> categoryService.update(categoryId, categoryRequestDto));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).existsByName(categoryRequestDto.getName());
    }

    @Test
    void deleteById_ExistingCategoryId_DeletesCategory() {
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        categoryService.deleteById(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void deleteById_NonExistingCategoryId_ThrowsEntityNotFoundException() {
        Long categoryId = 999L;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteById(categoryId));

        verify(categoryRepository, times(1)).existsById(categoryId);
    }
}
