package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    private Long category;

    @NotBlank
    @NotNull
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    @JsonProperty(defaultValue = "false")
    private Boolean paid = false;

    @PositiveOrZero
    @JsonProperty(defaultValue = "0")
    private Integer participantLimit = 0;

    @JsonProperty(defaultValue = "true")
    private Boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
