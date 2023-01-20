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

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("createItemRequest (POST /requests): Добавить новый запрос вещи (входной параметр) = {} " +
                "для пользователя {}", itemRequestDto, userId);
        ItemRequestDto resItemRequestDto = itemRequestService.create(userId, itemRequestDto);
        log.info("createItemRequest (POST /requests): Запрос на вещь добавлен: {}", resItemRequestDto);

        return resItemRequestDto;
    }

    //Получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestInfoDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getAllItemRequests (GET /requests/): Получить список своих (Ид пользователя {}) " +
                "запросов вместе с данными об ответах на них", userId);
        List<ItemRequestInfoDto> allItemRequestsInfoDto = itemRequestService.getAll(userId);
        log.info("getAllItemRequest (GET /requests/): Результат = {}", allItemRequestsInfoDto);

        return allItemRequestsInfoDto;
    }


    //Получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long requestId) {
        log.info("getItemRequestById (GET /requests/{}): Получить запрос на вещь по id(входной параметр) = {}",
                requestId, requestId);
        ItemRequestInfoDto itemRequestInfoDto = itemRequestService.getById(userId, requestId);
        log.info("getItemRequestById (GET /requests/{}): Результат = {}", requestId, itemRequestInfoDto);

        return itemRequestInfoDto;
    }

    //Получить список запросов, созданных другими пользователями
    @GetMapping("/all")
    public List<ItemRequestInfoDto> getAllWithPage(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "25") Integer size) {

        log.info("getAllWithPage (GET /requests/all?from={}&size={}): Поиск вещи потенциальным арендатором", from, size);
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequestInfoDto> allItemRequestsInfoDto = itemRequestService.getAllWithPage(userId, pageable);
        log.info("getAllWithPage (GET /requests/all): Результат = {}", allItemRequestsInfoDto);

        return allItemRequestsInfoDto;
    }

}
