package ru.practicum.service.user;

import org.springframework.stereotype.Service;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

@Service
public interface UserAdminService {

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
