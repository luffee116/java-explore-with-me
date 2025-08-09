package ru.practicum.mappers;

import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.NewCompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.entity.compilation.Compilation;
import ru.practicum.entity.event.Event;

import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : false)
                .events(events)
                .build();
    }

    public static CompilationDto toDto(Compilation compilation) {
        Set<EventShortDto> events = compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toSet());

        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(events)
                .build();
    }
}