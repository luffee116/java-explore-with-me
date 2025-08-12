package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventPublicService {
    List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size,
            HttpServletRequest request);

    EventFullDto getPublicEventById(Long id, HttpServletRequest request);

}
