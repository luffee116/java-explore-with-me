package ru.practicum.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCategoryDto {
    @Size(min = 2, max = 50)
    @NotNull
    private String name;
}
