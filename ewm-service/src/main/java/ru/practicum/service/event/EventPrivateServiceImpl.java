package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.entity.category.Category;
import ru.practicum.entity.event.Event;
import ru.practicum.entity.event.EventState;
import ru.practicum.entity.event.Location;
import ru.practicum.entity.user.User;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPrivateServiceImpl implements EventPrivateService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageRequest)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + "not found"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id " + newEventDto.getCategory() + "not found"));

        Location location = new Location();
        location.setLat(newEventDto.getLocation().getLat());
        location.setLon(newEventDto.getLocation().getLon());
        location = locationRepository.save(location);

        Event event = EventMapper.toEvent(newEventDto, initiator, category, location);
        Event savedEvent = eventRepository.save(event);
        log.info("Created new event with id {}", savedEvent.getId());
        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + "not found"));

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Only initiator can update event");
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot update published event");
        }

        if (updateRequest.getAnnotation() != null && !updateRequest.getAnnotation().isBlank()) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getDescription() != null && !updateRequest.getDescription().isBlank()) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategoryId()).orElseThrow(
                    () -> new NotFoundException("Category with id " + updateRequest.getCategoryId() + "not found"));
            event.setCategory(category);
        }

        if (updateRequest.getEventDate() != null) {
            if (updateRequest.getEventDate().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Event date cannot be before current date");
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

        if (updateRequest.getTitle() != null && !updateRequest.getTitle().isBlank()) {
            event.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new BadRequestException("State action not supported");
            }
        }

        Event updatedEvent = eventRepository.save(event);
        log.info("Updated event with id {}", updatedEvent.getId());
        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new NotFoundException("Event with id " + eventId + "not found");
        }
        log.info("Get event with id {}", event.getId());
        return EventMapper.toEventFullDto(event);
    }
}
