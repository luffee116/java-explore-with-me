package ru.practicum.service.request;

import org.springframework.stereotype.Service;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.requests.EventRequestStatusUpdateResult;

import java.util.List;

@Service
public interface RequestService {

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(
            Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

}
