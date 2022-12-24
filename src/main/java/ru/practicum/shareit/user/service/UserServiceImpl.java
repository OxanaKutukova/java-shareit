package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        validateEmail(userDto);

        return UserMapper.toUserDto(userRepository.create(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmail(userDto);
            user.setEmail(userDto.getEmail());
        }

        return UserMapper.toUserDto(userRepository.update(userId, user));
    }

    @Override
    public void delete(Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        userRepository.delete(userId);
    }


    public void validateEmail(UserDto userDto) {
        if (userRepository.findAll()
                .stream()
                .anyMatch(userO -> userO.getEmail().equals(userDto.getEmail()))) {
            throw new ConflictException("Пользователь с email = " + userDto.getEmail() +  "уже есть в базе");
        }
    }
}
