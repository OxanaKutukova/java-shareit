package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

public interface ItemService {

    List<ItemInfoDto> getByOwnerId(Long userId);

    ItemInfoDto getById(Long userId, Long itemId);

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    void delete(Long itemId);

    List<ItemDto> getByNameByDirector(String text);

    CommentDto createComment(Long userId, CommentDto commentDto, Long itemId);
}
