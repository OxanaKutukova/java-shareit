package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    @Autowired
    private final ItemService itemService;

    //Получить список всех вещей пользователя
    @GetMapping
    public List<ItemInfoDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getAllItems (GET /items/): Получить список всех вещей пользователя: {}", userId);
        List<ItemInfoDto> allItems = itemService.getByOwnerId(userId);
        log.info("getAllItems (GET /items/): Результат = {}", allItems);

        return allItems;
    }

    //Получить вещь по Id
    @GetMapping("/{itemId}")
    public ItemInfoDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        log.info("getItemById (GET /items/{}): Получить вещь по id(входной параметр) = {}", itemId, itemId);
        ItemInfoDto itemInfoDto = itemService.getById(userId, itemId);
        log.info("getItemById (GET /items/{}): Результат = {}", itemId, itemInfoDto);

        return itemInfoDto;
    }

    //Добавить вещь
    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("createItem (POST /items/): Добавить вещь (входной параметр) = {} для пользователя {}", itemDto, userId);
        ItemDto resItemDto = itemService.create(userId, itemDto);
        log.info("createItem (POST /items/): Вещь добавлена: {}", resItemDto);

        return resItemDto;
    }

    //Изменить вещь
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("updateItem (PATCH /items/{}): Изменить вещь с Id: {} на {} у пользователя {}", itemId, itemId, itemDto, userId);
        ItemDto resItemDto = itemService.update(userId, itemId, itemDto);
        log.info("updateItem (PATCH /items/{}): Вещь изменена: {}", itemId, resItemDto);

        return resItemDto;
    }

    //Удалить вещь
    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("deleteItem (DELETE /items/{}): Удалить вещь с Id = {}", itemId, itemId);
        itemService.delete(itemId);
        log.info("deleteItem (DELETE /items/): Вещь удалена: {}", itemId);
    }

    //Поиск вещи потенциальным арендатором.
    @GetMapping("/search")
    public List<ItemDto> getByNameByDirector(@RequestParam(required = false) String text) {
        log.info("getByNameByDirector (GET /items/search?text={}): Поиск вещи потенциальным арендатором", text);
        List<ItemDto> allItems = itemService.getByNameByDirector(text);
        log.info("getByNameByDirector (GET /items/search?text={}): Результат = {}", text, allItems);

        return allItems;
    }

    //Добавить комментарий к вещи
    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId) {
        log.info("createComment (POST /items/{}/comment): Добавить комментарий (входной параметр) = {} к вещи с Id {}",
                itemId, commentDto, itemId);
        CommentDto resCommentDto = itemService.createComment(userId, commentDto, itemId);
        log.info("createComment (POST /items/{}/comment): Комментарий добавлен: {}", itemId, resCommentDto);

        return resCommentDto;
    }

}
