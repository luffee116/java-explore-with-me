package ru.practicum.mappers;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.entity.category.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
