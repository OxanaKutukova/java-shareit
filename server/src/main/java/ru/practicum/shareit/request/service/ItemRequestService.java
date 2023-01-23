package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestInfoDto getById(Long userId, Long itemRequestId);

    List<ItemRequestInfoDto> getAll(Long userId);

    List<ItemRequestInfoDto> getAllWithPage(Long userId, Pageable pageable);
}
