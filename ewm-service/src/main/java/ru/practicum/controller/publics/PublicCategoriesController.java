package ru.practicum.controller.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryPublicService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoriesController {
    private final CategoryPublicService categoryService;

    @GetMapping
    public List<CategoryDto> getAll(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }

}
