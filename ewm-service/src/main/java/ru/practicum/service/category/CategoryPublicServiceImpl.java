package ru.practicum.service.category;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.entity.category.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryPublicServiceImpl implements CategoryPublicService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository
                .findAll(pageRequest)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Category with id %s not found", id)));
        return toDto(category);
    }

    private CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

}
