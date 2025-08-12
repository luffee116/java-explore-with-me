package ru.practicum.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private UserShortDto initiator;
    private String eventDate;
    private Boolean paid;
    private Long views;
    private Long confirmedRequests;
}
