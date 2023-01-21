package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@RestController
@Validated
@Slf4j
@RequestMapping("/items")

public class ItemController {

    @Autowired
    private final ItemService itemService;

    //Получить список всех вещей пользователя
    @GetMapping
    public List<ItemInfoDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @RequestParam(name = "size", defaultValue = "25") Integer size) {
        log.info("Server: Получить список всех вещей пользователя с id = {}", userId);
        final Pageable pageable = PageRequest.of(from / size, size);
        List<ItemInfoDto> allItems = itemService.getByOwnerId(userId, pageable);
        log.info("Server: Получен список всех вещей пользователя с id = {}. Результат = {}", userId, allItems);

        return allItems;
    }

    //Получить вещь по Id
    @GetMapping("/{itemId}")
    public ItemInfoDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        log.info("Server: Получить вещь с id = {}", itemId);
        ItemInfoDto itemInfoDto = itemService.getById(userId, itemId);
        log.info("Server: Получена вещь с id = {}. Результат = {}", itemId, itemInfoDto);

        return itemInfoDto;
    }

    //Добавить вещь
    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        log.info("Server: Добавить вещь: {} для пользователя {}", itemDto, userId);
        ItemDto resItemDto = itemService.create(userId, itemDto);
        log.info("Server: Вещь: {} для пользователя с id {} добавлена.", resItemDto, userId);

        return resItemDto;
    }

    //Изменить вещь
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Server: Обновить вещь с id {} на {} у пользователя {}", itemId, itemDto, userId);
        ItemDto resItemDto = itemService.update(userId, itemId, itemDto);
        log.info("Server: Обновлена вещь с id = {}, следующими данными: {}", itemId, resItemDto);

        return resItemDto;
    }

    //Удалить вещь
    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Server: Удалить вещь с id {} у пользователя {}", itemId, userId);
        itemService.delete(itemId);
        log.info("Server: Удалена вещь с id {}", itemId);
    }

    //Поиск вещи потенциальным арендатором.
    @GetMapping("/search")
    public List<ItemDto> getByNameByDirector(@RequestParam(required = false) String text,
                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", defaultValue = "25") Integer size) {
        log.info("Server: Поиск вещи потенциальным арендатором");
        final Pageable pageable = PageRequest.of(from / size, size);
        List<ItemDto> allItems = itemService.getByNameByDirector(text, pageable);
        log.info("Server: Результат поиска вещи (параметр поиска - {}) потенциальным арендатором: {}", text, allItems);

        return allItems;
    }

    //Добавить комментарий к вещи
    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId) {
        log.info("Server: Добавить комментарий  = {} к вещи с id {}", commentDto, itemId);
        CommentDto resCommentDto = itemService.createComment(userId, commentDto, itemId);
        log.info("Server: Комментарий  = {} к вещи с id {} добавлен.", resCommentDto, itemId);

        return resCommentDto;
    }

}
