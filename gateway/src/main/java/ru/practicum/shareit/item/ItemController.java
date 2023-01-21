package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    //Получить список всех вещей пользователя
    @GetMapping
    public ResponseEntity<Object> getByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "25") Integer size) {
        log.info("Gateway: Получен список всех вещей пользователя с id = {}", userId);

        return itemClient.getByOwnerId(userId, from, size);
    }

    //Получить вещь по Id
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long itemId) {
        log.info("Gateway: Получена вещь с id = {}", itemId);

        return itemClient.getById(userId, itemId);
    }

    //Добавить вещь
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Gateway: Добавлена вещь = {}", itemDto);

        return itemClient.create(userId, itemDto);
    }

    //Изменить вещь
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Gateway: Обновлена вещь = {}", itemDto);

        return itemClient.update(userId, itemId, itemDto);
    }

    //Удалить вещь
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Gateway: Удалена вещь с id {}", itemId);

        return itemClient.delete(userId, itemId);
    }

    //Поиск вещи потенциальным арендатором.
    @GetMapping("/search")
    public ResponseEntity<Object> getByNameByDirector(
            @RequestParam(required = false) String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "25") Integer size) {
        log.info("Gateway: Поиск вещи потенциальным арендатором по параметр поиска - {}", text);

        return itemClient.getByNameDirector(text, from, size);
    }

    //Добавить комментарий к вещи
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @PathVariable Long itemId) {
        log.info("Gateway: Добавлен комментарий {} к к вещи с id {}", commentDto, itemId);

        return itemClient.createComment(userId, itemId, commentDto);
    }
}
