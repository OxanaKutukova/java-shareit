package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> getByOwnerId(Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return itemRepository.findByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long itemId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найден"));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {

        //Проверим пользователя
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        item = itemRepository.create(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {

        //Проверим пользователя
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        //Проверим вещь
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найден"));

        //Проверим владельца вещи
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " может изменять только владелец");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item.setOwner(user);

        return ItemMapper.toItemDto(itemRepository.update(itemId, item));
    }

    @Override
    public void delete(Long itemId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найден"));
        itemRepository.delete(itemId);
    }

    @Override
    public List<ItemDto> findByNameByDirector(String text) {
        return itemRepository.findByNameByDirector(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
