package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public List<ItemDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("getAllItems (GET /items/): Получить список всех вещей пользователя: {}", userId);
        List<ItemDto> allItems = itemService.getAllItemsByUserId(userId);
        log.info("getAllItems (GET /items/): Результат = {}", allItems);
        return allItems;
    }

    //Получить вещь по Id
    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("getItemById (GET /items/{}): Получить вещь по id(входной параметр) = {}", itemId, itemId);
        ItemDto itemDto = itemService.getItemById(itemId);
        log.info("getItemById (GET /items/{}): Результат = {}", itemId, itemDto);
        return itemDto;
    }

    //Добавить вещь
    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("createItem (POST /items/): Добавить вещь (входной параметр) = {} для пользователя {}", itemDto, userId);
        ItemDto resItemDto = itemService.createItem(userId, itemDto);
        log.info("createItem (POST /items/): Вещь добавлена: {}", resItemDto);
        return resItemDto;
    }

    //Изменить вещь
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("updateItem (PATCH /items/{}): Изменить вещь с Id: {} на {} у пользователя {}", itemId, itemId, itemDto, userId);
        ItemDto resItemDto = itemService.updateItem(userId, itemId, itemDto);
        log.info("updateItem (PATCH /items/{}): Вещь изменена: {}", itemId, resItemDto);
        return resItemDto;
    }

    //Удалить вещь
    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId) {
        log.info("deleteItem (DELETE /items/{}): Удалить вещь с Id = {}", itemId, itemId);
        itemService.deleteItem(itemId);
        log.info("deleteItem (DELETE /items/): Вещь удалена: {}", itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByNameByDirector(@RequestParam(required = false) String text) {
        return itemService.searchItemsByNameByDirector(text);
    }

}
