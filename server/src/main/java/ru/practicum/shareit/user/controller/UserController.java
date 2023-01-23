package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    @Autowired
    private final UserService userService;

    //Получить список всех пользователей
    @GetMapping
    public List<UserDto> getAll() {
        log.info("Server: Получить список всех пользователей.");
        List<UserDto> allUsers = userService.getAll();
        log.info("Server: Получен список всех пользователей. Результат = {}", allUsers);

        return allUsers;
    }

    //Получить пользователя по Id
    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("Server: Получить пользователя с id = {}.", userId);
        UserDto userDto = userService.getById(userId);
        log.info("Server: Получен пользователь с id = {}. Результат = {}", userId, userDto);

        return userDto;
    }

    //Добавить пользователя
    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Server: Добавить пользователя = {}", userDto);
        UserDto resUserDto = userService.create(userDto);
        log.info("Server: Пользователь добавлен: {}", resUserDto);

        return resUserDto;
    }

    //Изменить пользователя
    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Server: Обновить пользователя с Id: {} на {}", userId, userDto);
        UserDto resUserDto = userService.update(userId, userDto);
        log.info("Server: Обновлен пользователь с id = {}, следующими данными: {}", userId, resUserDto);

        return resUserDto;
    }

    //Удалить пользователя
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Server: Удалить пользователя с Id = {}", userId);
        userService.delete(userId);
        log.info("Server: Удален пользователь с id = {}", userId);
    }
}
