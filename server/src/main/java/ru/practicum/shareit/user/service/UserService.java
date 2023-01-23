package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getById(Long userId);

    UserDto create(UserDto itemDto);

    UserDto update(Long userId, UserDto itemDto);

    void delete(Long userId);

}
