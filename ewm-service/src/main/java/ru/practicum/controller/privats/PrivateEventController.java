package ru.practicum.controller.privats;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.service.event.EventPrivateService;
import ru.practicum.service.request.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final RequestService requestService;
    private final EventPrivateService eventPrivateService;

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable("userId") Long userId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        return eventPrivateService.getEventsByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return eventPrivateService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUserIdAndEventId(@PathVariable("userId") Long userId,
                                                   @PathVariable("eventId") Long eventId) {
        return eventPrivateService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")

    public EventFullDto updateEventByUser(@PathVariable("userId") Long userId,
                                          @PathVariable("eventId") Long eventId,
                                          @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventPrivateService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        return requestService.updateRequestStatus(userId, eventId, updateRequest);
    }
}
