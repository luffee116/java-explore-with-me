package ru.practicum.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.entity.request.RequestStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {

    private LocalDateTime created;

    private Long event;

    private Long id;

    private Long requester;

    private RequestStatus status;
}
