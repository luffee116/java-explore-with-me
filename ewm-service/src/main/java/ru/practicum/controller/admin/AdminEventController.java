package ru.practicum.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.event.EventAdminService;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventAdminService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        return eventService.searchEvents(users, states, categories, rangeStart, rangeEnd, from, size, request);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventAdminRequest updateRequest) {

        return eventService.updateEventByAdmin(eventId, updateRequest);
    }
}