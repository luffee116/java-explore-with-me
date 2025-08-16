package ru.practicum.mappers;

import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.user.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
