package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.entity.user.User;
import ru.practicum.entity.event.EventState;
import ru.practicum.entity.request.ParticipationRequest;
import ru.practicum.entity.request.RequestStatus;
import ru.practicum.entity.request.StatusAction;
import ru.practicum.repository.EventRepository;
import ru.practicum.entity.event.Event;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        if (eventRepository.findByIdAndInitiatorId(eventId, userId) == null) {
            throw new NotFoundException("Event with id " + eventId + " not found or user with id"
                    + userId + " is not initiator");
        }

        return requestRepository.findAllByEventId(eventId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(
            Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {

        Event event = findEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("User is not event initiator");
        }

        if (!event.getRequestModeration()) {
            throw new ConflictException("Event does not require request moderation");
        }

        if (event.getParticipantLimit() == 0) {
            throw new ConflictException("Event does not require participant limit");
        }

        List<ParticipationRequest> requests = requestRepository
                .findAllByIdInAndEventIdAndInitiatorId(
                        updateRequest.getRequestIds(),
                        eventId,
                        userId);

        if (requests.size() != updateRequest.getRequestIds().size()) {
            throw new NotFoundException("Some requests not found");
        }

        List<ParticipationRequest> updatedRequests = new ArrayList<>();
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        long confirmedCount = requestRepository.countRequestsByEventAndStatus(
                eventId, RequestStatus.CONFIRMED);
        int availableSlots = event.getParticipantLimit() - (int) confirmedCount;

        if (availableSlots == 0) {
            throw new ConflictException("Event does not have available slots");
        }

        for (ParticipationRequest request : requests) {
            if (request.getRequestStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request must be in PENDING state");
            }

            if (updateRequest.getStatus() == StatusAction.CONFIRMED && availableSlots > 0) {
                request.setRequestStatus(RequestStatus.CONFIRMED);
                confirmed.add(toDto(request));
                availableSlots--;
            } else {
                request.setRequestStatus(RequestStatus.REJECTED);
                rejected.add(toDto(request));
            }
            updatedRequests.add(request);
        }

        // Сохраняем все изменения одним запросом
        requestRepository.saveAll(updatedRequests);

        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        findUserById(userId);

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User requester = findUserById(userId);

        Event event = findEventById(eventId);

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Request already exists with eventId " + eventId + " and userId " + userId);
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator can't participate in own event");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published");
        }

        if (event.getParticipantLimit() > 0) {
            long confirmedCount = requestRepository.countRequestsByEventAndStatus(
                    eventId, RequestStatus.CONFIRMED);
            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Participant limit reached");
            }
        }

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            ParticipationRequest request = ParticipationRequest.builder()
                    .event(event)
                    .requester(requester)
                    .created(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                    .requestStatus(RequestStatus.CONFIRMED)
                    .build();
            return toDto(requestRepository.save(request));
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .event(event)
                .requester(requester)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                .requestStatus(event.getRequestModeration() ?
                        RequestStatus.PENDING :
                        RequestStatus.CONFIRMED)
                .build();

        return toDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findParticipationRequestByIdAndRequesterId(requestId, userId);

        if (request == null) {
            throw new NotFoundException("Request with id " + requestId + " not found");
        }

        if (request.getRequestStatus() == RequestStatus.CANCELED) {
            throw new ConflictException("Request already canceled");
        }

        request.setRequestStatus(RequestStatus.CANCELED);
        return toDto(requestRepository.save(request));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id " + userId + " not found"));
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id " + eventId + " not found"));
    }

    private ParticipationRequestDto toDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getRequestStatus())
                .build();
    }

}
