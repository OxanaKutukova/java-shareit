package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto createUser(UserDto itemDto);

    UserDto updateUser(Long userId, UserDto itemDto);

    void deleteUser(Long userId);
}
