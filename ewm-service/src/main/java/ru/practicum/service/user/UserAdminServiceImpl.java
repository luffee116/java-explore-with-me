package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.user.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserAdminServiceImpl implements UserAdminService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<User> users = ids == null || ids.isEmpty()
                ? userRepository.findAll(pageRequest).getContent()
                : userRepository.findByIdIn(ids, pageRequest);

        log.info("getUsers {}", users);
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ConflictException("User with email " + newUserRequest.getEmail() + " already exists");
        }

        User user = User.builder()
                .email(newUserRequest.getEmail())
                .name(newUserRequest.getName()).build();

        User savedUser = userRepository.save(user);
        log.info("createUser {}", savedUser);
        return toDto(savedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
