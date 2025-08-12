package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDto;

import java.util.List;

public interface CategoryPublicService {
    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(Long id);
}
