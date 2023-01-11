package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
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
        final User user = getUserById(userId);

        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        final User userS = userRepository.save(user);

        return UserMapper.toUserDto(userS);
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        final User userU = getUserById(userId);
        if (user.getName() != null) {
            userU.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userU.setEmail(user.getEmail());
        }
        userRepository.save(userU);

        return UserMapper.toUserDto(userU);
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        throwIfNotExistUser(userId);
        userRepository.deleteById(userId);
    }

    private User getUserById(Long userId) {
        return  userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private void throwIfNotExistUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}
