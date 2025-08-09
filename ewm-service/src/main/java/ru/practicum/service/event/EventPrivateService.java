package ru.practicum.service.event;

import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;

import java.util.List;

@Service
public interface EventPrivateService {
    List<EventShortDto> getEventsByUser(Long userId, int from, int size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);
}
