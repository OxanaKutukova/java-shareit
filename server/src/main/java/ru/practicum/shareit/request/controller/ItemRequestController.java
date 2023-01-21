package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    @Autowired
    private final ItemRequestService itemRequestService;

    //Добавить новый запрос вещи
    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Server:  Добавить запрос на вещь = {} от пользователя с id {}", itemRequestDto, userId);
        ItemRequestDto resItemRequestDto = itemRequestService.create(userId, itemRequestDto);
        log.info("Server: Добавлен запрос на вещь = {} от пользователя с id {}", resItemRequestDto, userId);

        return resItemRequestDto;
    }

    //Получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestInfoDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Server: Получить список запросов от пользователя с id {}", userId);
        List<ItemRequestInfoDto> allItemRequestsInfoDto = itemRequestService.getAll(userId);
        log.info("Server: Получен список запросов от пользователя с id {}. Результат = {}", userId, allItemRequestsInfoDto);

        return allItemRequestsInfoDto;
    }


    //Получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long requestId) {
        log.info("Server: Получить запрос с id {}", requestId);
        ItemRequestInfoDto itemRequestInfoDto = itemRequestService.getById(userId, requestId);
        log.info("Server: Получен запрос с id = {}. Результат = {}", requestId, itemRequestInfoDto);

        return itemRequestInfoDto;
    }

    //Получить список запросов, созданных другими пользователями
    @GetMapping("/all")
    public List<ItemRequestInfoDto> getAllWithPage(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "25") Integer size) {

        log.info("Server: Поиск вещи потенциальным арендатором с id = {}", userId);
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequestInfoDto> allItemRequestsInfoDto = itemRequestService.getAllWithPage(userId, pageable);
        log.info("Server: Поиск вещи потенциальным арендатором с id = {}. Результат = {}", userId, allItemRequestsInfoDto);

        return allItemRequestsInfoDto;
    }

}
