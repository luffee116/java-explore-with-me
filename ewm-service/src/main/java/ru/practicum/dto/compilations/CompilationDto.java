package ru.practicum.dto.compilations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.EventShortDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CompilationDto {
    private Set<EventShortDto> events;

    private Long id;

    private Boolean pinned;

    private String title;

}
