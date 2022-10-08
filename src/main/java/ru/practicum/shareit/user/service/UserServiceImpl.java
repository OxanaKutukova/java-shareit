package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        final UserDto resUser = getUserByIdWithCheckNull(userId);
        return resUser;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto);
        return UserMapper.toUserDto(userRepository.createUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        final UserDto uUser = getUserByIdWithCheckNull(userId);
        if (userDto.getName() != null) {
            uUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmail(userDto);
            uUser.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.updateUser(userId, UserMapper.toUser(uUser)));
    }

    @Override
    public void deleteUser(Long userId) {
        final UserDto user = getUserByIdWithCheckNull(userId);
        userRepository.deleteUser(userId);
    }

    private UserDto getUserByIdWithCheckNull(Long userId) {
        if (userId == null) {
            throw new ValidationException("Идентификатор пользователя не может быть равен null");
        }
        final UserDto resUser = UserMapper.toUserDto(userRepository.getUserById(userId));
        if (resUser == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return resUser;
    }

    public void validateEmail(UserDto userDto) {
        if (userRepository.getAllUsers().stream().anyMatch(userO -> userO.getEmail().equals(userDto.getEmail()))) {
            throw new ValidationException("Пользователь с email = " + userDto.getEmail() +  "уже есть в базе");
        }
    }
}
