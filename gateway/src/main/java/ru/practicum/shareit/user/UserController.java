package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Gateway: Добавлен пользователь = {}", userDto);

        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Min(1) @NotNull @PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Gateway: Обновлен пользователь с id = {}, следующими данными: {}", userId, userDto);

        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@Min(1) @NotNull @PathVariable Long userId) {
        log.info("Gateway: Удален пользователь с id = {}", userId);

        return userClient.delete(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@Min(1) @NotNull @PathVariable Long userId) {
        log.info("Gateway: Получен пользователь с id = {}", userId);

        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Gateway: Получен список всех пользователей.");

        return userClient.getAll();
    }
}