package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
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
        log.info("getAllUsers (GET /users/): Получить список всех пользователей");
        List<UserDto> allUsers = userService.getAll();
        log.info("getAllUsers (GET /users/): Результат = {}", allUsers);

        return allUsers;
    }

    //Получить пользователя по Id
    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("getUserById (GET /users/{}): Получить пользователя по id(входной параметр) = {}", userId, userId);
        UserDto userDto = userService.getById(userId);
        log.info("getUserById (GET /users/{}): Результат = {}", userId, userDto);

        return userDto;
    }

    //Добавить пользователя
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("createUser (POST /users/): Добавить пользователя (входной параметр) = {}", userDto);
        UserDto resUserDto = userService.create(userDto);
        log.info("createUser (POST /users/): Пользователь добавлен: {}", resUserDto);

        return resUserDto;
    }

    //Изменить пользователя
    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("updateUser (PATCH /users/{}): Изменить пользователя с Id: {} на {}", userId, userId, userDto);
        UserDto resUserDto = userService.update(userId, userDto);
        log.info("updateUser (PATCH /users/{}): Пользователь изменен: {}", userId, resUserDto);

        return resUserDto;
    }

    //Удалить пользователя
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("deleteUser (DELETE /users/{}): Удалить пользователя с Id = {}", userId, userId);
        userService.delete(userId);
        log.info("deleteUser (DELETE /users/): Пользователь удален: {}", userId);
    }
}
