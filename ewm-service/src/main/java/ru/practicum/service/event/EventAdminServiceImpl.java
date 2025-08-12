package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.*;
import ru.practicum.entity.event.*;
import ru.practicum.entity.request.RequestStatus;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.service.statsClient.EventStatsClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final EventStatsClient statsClient;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size,
            HttpServletRequest request) {

        List<EventState> stateEnums = null;
        if (states != null) {
            stateEnums = states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        }

        PageRequest page = PageRequest.of(from / size, size);
        Page<Event> events = eventRepository.searchEventsByAdmin(
                users,
                stateEnums,
                categories,
                rangeStart != null ? rangeStart : LocalDateTime.now(),
                rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(10),
                page);

        Map<Long, Long> confirmedRequests = getConfirmedRequestsCount(events.getContent());

        statsClient.recordEventView(request);

        Map<Long, Long> viewsMap = statsClient.getEventsViews(
                events.getContent().stream()
                        .map(Event::getId)
                        .collect(Collectors.toList()));

        return events.map(event -> EventMapper.toEventFullDto(
                event,
                viewsMap.getOrDefault(event.getId(), 0L),
                confirmedRequests.getOrDefault(event.getId(), 0L))
        ).getContent();

    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Cannot publish event not in PENDING state");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Cannot reject already published event");
                    }
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategoryId() != null) {
            event.setCategory(categoryRepository.findById(updateRequest.getCategoryId()).get());
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            if (updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Event date must be at least 1 hour from now");
            }
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(new Location(
                    null,
                    updateRequest.getLocation().getLat(),
                    updateRequest.getLocation().getLon()
            ));
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        Event updatedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(
                updatedEvent);
    }

    private Map<Long, Long> getConfirmedRequestsCount(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<Object[]> results = requestRepository.countByEventIdInAndStatus(
                eventIds,
                RequestStatus.CONFIRMED);

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],  // eventId
                        result -> (Long) result[1]   // count
                ));
    }
}