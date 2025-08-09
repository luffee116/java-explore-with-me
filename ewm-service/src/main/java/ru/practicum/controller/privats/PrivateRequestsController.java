package ru.practicum.controller.privats;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.service.request.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestsController {
    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getUserRequests(@PathVariable("userId") Long userId) {
        return requestService.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUserRequest(@PathVariable("userId") Long userId,
                                                     @RequestParam Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelUserRequest(@PathVariable("userId") Long userId,
                                                     @RequestParam Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

}
