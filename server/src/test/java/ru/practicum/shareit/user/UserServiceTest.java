package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;


    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getById() {
        User user = new User();
        user.setId(1L);
        user.setName("Oksi");
        user.setEmail("oksi.dto@ya.ru");
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UserDto result = userService.getById(user.getId());

        Assertions.assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void getAll() {
        UserDto userDto = new UserDto(1L, "Oksi", "oksi.dto@ya.ru");
        User user = new User();
        user.setId(1L);
        user.setName("Oksi");
        user.setEmail("oksi.dto@ya.ru");
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user));

        List<UserDto> result = userService.getAll();

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    void create() {
        UserDto userDto = new UserDto(1L, "Oksi", "oksi.dto@ya.ru");
        when(userRepository.save(UserMapper.toUser(userDto)))
                .thenReturn(UserMapper.toUser(userDto));

        UserDto result = userService.create(userDto);

        Assertions.assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void update() {
        UserDto userDto = new UserDto(1L, "Oksi", "oksi.dto@ya.ru");
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(UserMapper.toUser(userDto)))
                .thenReturn(UserMapper.toUser(userDto));

        UserDto result = userService.update(user.getId(), userDto);

        Assertions.assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void delete() {
        UserDto userDto = new UserDto(1L, "Oksi", "oksi.dto@ya.ru");
        when(userRepository.findById(UserMapper.toUser(userDto).getId()))
                .thenReturn(Optional.of(UserMapper.toUser(userDto)));

        userService.delete(userDto.getId());

        verify(userRepository, times(1)).deleteById(userDto.getId());
    }

    @Test
    public void getByUnknownId() {

        Throwable throwable = assertThrows(NotFoundException.class, () -> userService.getById(100L));

        assertEquals("Пользователь с id=100 не найден", throwable.getMessage(),
                "Неверный идентификатор пользователя");
    }

    @Test
    public void deleteUnknownId() {

        Throwable throwable = assertThrows(NotFoundException.class, () -> userService.delete(100L));

        assertEquals("Пользователь с id=100 не найден", throwable.getMessage(),
                "Неверный идентификатор пользователя");
    }
}