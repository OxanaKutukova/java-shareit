package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    @Autowired
    private final ItemRequestRepository itemRequestRepository;
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        //Проверим пользователя
        final User user = getUserById(userId);
        final ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        final ItemRequest itemRequestS = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(itemRequestS);
    }

    @Override
    public ItemRequestInfoDto getById(Long userId, Long itemRequestId) {
        throwIfNotExistUser(userId);
        final ItemRequest itemRequest = getItemRequestById(itemRequestId);
        final ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(itemRequest);
        List<Item> items = itemRepository.findAllByItemRequest_Id(itemRequest.getId());

        if (!items.isEmpty()) {
            itemRequestInfoDto.setItems(items
                    .stream().map(ItemMapper::toItemDto)
                    .collect(Collectors.toList())
            );
        }

        return itemRequestInfoDto;
    }

    @Override
    public List<ItemRequestInfoDto> getAll(Long userId) {
        throwIfNotExistUser(userId);
        final List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(userId, Sort.by(Sort.Direction.DESC, "created"));
        final List<ItemRequestInfoDto> res = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {

            final ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(itemRequest);
            List<Item> items = itemRepository.findAllByItemRequest_Id(itemRequest.getId());

            if (!items.isEmpty()) {
                itemRequestInfoDto.setItems(items
                        .stream().map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())
                );
            }
            res.add(itemRequestInfoDto);
        }

        return res;
    }

    @Override
    public List<ItemRequestInfoDto> getAllWithPage(Long userId, Pageable pageable) {
        throwIfNotExistUser(userId);
        final List<ItemRequest> itemRequests = itemRequestRepository.findAllByNotRequestorId(userId, pageable);
        final List<ItemRequestInfoDto> res = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {

            final ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(itemRequest);
            List<Item> items = itemRepository.findAllByItemRequest_Id(itemRequest.getId());

            if (!items.isEmpty()) {
                itemRequestInfoDto.setItems(items
                        .stream().map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())
                );
            }
            res.add(itemRequestInfoDto);
        }

        return res;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private void throwIfNotExistUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private ItemRequest getItemRequestById(Long itemRequestId) {
        return itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос на вещь с id=" + itemRequestId + " не найден"));
    }
}
