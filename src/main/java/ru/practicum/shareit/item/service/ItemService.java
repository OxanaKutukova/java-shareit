package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getByOwnerId(Long userId);

    ItemDto getById(Long itemId);

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    void delete(Long itemId);

    List<ItemDto> findByNameByDirector(String text);

}
