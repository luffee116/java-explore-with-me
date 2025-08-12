package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long categoryId;

    @Size(min = 20, max = 7000)
    private String description;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid;

    @Positive
    private Integer participantLimit;
    private Boolean requestModeration;

    @Size(min = 3, max = 120)
    private String title;

    private UserStateAction stateAction;

    public enum UserStateAction {
        SEND_TO_REVIEW,  // Отправить на модерацию
        CANCEL_REVIEW    // Отменить отправку
    }
}
