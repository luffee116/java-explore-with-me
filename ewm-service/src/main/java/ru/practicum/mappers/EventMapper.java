package ru.practicum.mappers;

import ru.practicum.entity.category.Category;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.LocationDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.entity.event.Event;
import ru.practicum.entity.event.EventState;
import ru.practicum.entity.event.Location;
import ru.practicum.entity.user.User;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {

    public static Event toEvent(NewEventDto newEventDto, User initiator, Category category) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(category)
                .initiator(initiator)
                .createdOn(LocalDateTime.now())
                .eventDate(newEventDto.getEventDate())
                .location(toLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .views(0L)
                .confirmedRequests(0L)
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto, User initiator, Category category, Location location) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(category)
                .initiator(initiator)
                .createdOn(LocalDateTime.now())
                .eventDate(newEventDto.getEventDate())
                .location(location)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .views(0L)
                .confirmedRequests(0L)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return toEventFullDto(event, event.getViews(), event.getConfirmedRequests(), event.getComments());
    }

    public static EventFullDto toEventFullDto(Event event, Long views, Long confirmedRequests, Long comments) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(toCategoryDto(event.getCategory()))
                .initiator(toUserShortDto(event.getInitiator()))
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .publishedOn(event.getPublishedOn())
                .location(toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .views(views != null ? views : 0L)
                .confirmedRequests(confirmedRequests != null ? confirmedRequests : 0L)
                .comments(comments != null ? comments : 0L)
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return toEventShortDto(event, event.getViews(), event.getConfirmedRequests(), event.getComments());
    }

    public static EventShortDto toEventShortDto(Event event, Long views, Long confirmedRequests, Long comments) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .initiator(toUserShortDto(event.getInitiator()))
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .paid(event.getPaid())
                .views(views != null ? views : 0L)
                .confirmedRequests(confirmedRequests != null ? confirmedRequests : 0L)
                .comments(comments != null ? comments : 0L)
                .build();
    }

    // Вспомогательные методы маппинга
    private static Location toLocation(LocationDto locationDto) {
        return new Location(null, locationDto.getLat(), locationDto.getLon());
    }

    private static LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }

    private static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    private static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
