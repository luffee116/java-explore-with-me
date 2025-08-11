package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.entity.event.Event;
import ru.practicum.entity.event.EventState;
import ru.practicum.entity.request.RequestStatus;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.service.statsClient.EventStatsClient;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {
    private final EventRepository eventRepository;
    private final EventStatsClient statsClient;
    private final ParticipationRequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size,
            HttpServletRequest request) {

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Range end cannot be before range start");
        }

        LocalDateTime start = (rangeStart != null) ? rangeStart : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? rangeEnd : LocalDateTime.now().plusYears(10);

        PageRequest pageRequest = switchPageRequest(sort, from, size);

        Page<Event> eventsPage = eventRepository.findPublicEvents(
                (text == null || text.isBlank()) ? null : text,
                (categories == null || categories.isEmpty()) ? null : categories,
                paid,
                start,
                end,
                onlyAvailable != null ? onlyAvailable : false,
                pageRequest
        );

        List<Event> events = eventsPage.getContent();

        statsClient.recordEventView(request);

        Map<Long, Long> views = statsClient.getEventsViews(
                events.stream().map(Event::getId).collect(Collectors.toList())
        );

        Map<Long, Long> confirmedRequests = getConfirmedRequestsCount(events);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        views.getOrDefault(event.getId(), 0L),
                        confirmedRequests.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());

        // Если сортировка по просмотрам, сортируем здесь
        if ("VIEWS".equalsIgnoreCase(sort)) {
            eventShortDtos.sort(Comparator.comparingLong(EventShortDto::getViews).reversed());
        }

        return eventShortDtos;
    }

    @Override
    @Transactional
    public EventFullDto getPublicEventById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        // Фиксируем просмотр
        statsClient.recordEventView(request);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Получаем статистику
        Long confirmedRequests = requestRepository.countRequestsByEventAndStatus(id, RequestStatus.CONFIRMED);
        Long views = statsClient.getEventsViews(List.of(id)).getOrDefault(id, 0L);

        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

    private Map<Long, Long> getConfirmedRequestsCount(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        return requestRepository.countConfirmedRequestsByEventIds(eventIds, RequestStatus.CONFIRMED);
    }

    private PageRequest switchPageRequest(String sort, int from, int size) {
        if ("EVENT_DATE".equalsIgnoreCase(sort)) {
            return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        } else if ("VIEWS".equalsIgnoreCase(sort)) {
            return PageRequest.of(from / size, size);
        } else {
            return PageRequest.of(from / size, size);
        }
    }
}
