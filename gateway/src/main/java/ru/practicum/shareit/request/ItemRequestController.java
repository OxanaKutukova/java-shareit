package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    //Добавить новый запрос вещи
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Gateway: Добавлен запрос на вещь = {} от пользователя с id {}", itemRequestDto, userId);

        return itemRequestClient.create(userId, itemRequestDto);
    }

    //Получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway: Получен список запросов от пользователя с id {}", userId);

        return itemRequestClient.getAll(userId);
    }


    //Получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long requestId) {
        log.info("Gateway: Получен запрос с id = {}", requestId);

        return itemRequestClient.getById(userId, requestId);
    }

    //Получить список запросов, созданных другими пользователями
    @GetMapping("/all")
    public ResponseEntity<Object> getAllWithPage(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "25") Integer size) {
        log.info("Gateway: Поиск вещи потенциальным арендатором с id = {}", userId);

        return itemRequestClient.getAllWithPage(userId, from, size);
    }
}
