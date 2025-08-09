package ru.practicum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private String email;
    private Long id;
    private String name;
}
