package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

public interface CategoryAdminService {
    CategoryDto createCategory(NewCategoryDto newCategory);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, NewCategoryDto categoryDto);

}
