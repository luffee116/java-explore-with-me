package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface EventAdminService {

    List<EventFullDto> searchEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size,
            HttpServletRequest request);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest);
}
