package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
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
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        UserDto  userDto = getUserByIdWithCheckNull(userId);
        return itemRepository.getAllItems()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        final ItemDto resItemDto = getItemByIdWithCheckNull(itemId);
        return resItemDto;
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {

        //Проверим пользователя
        UserDto  userDto = getUserByIdWithCheckNull(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userDto));
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        //Проверим пользователя
        final UserDto userDto = getUserByIdWithCheckNull(userId);
        //Проверим вещь
        final ItemDto uItemDto = getItemByIdWithCheckNull(itemId);
        //Проверим пользователя вещи
        if (!uItemDto.getOwnerId().equals(userId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " может изменять только владелец");
        }
        if (itemDto.getName() != null) {
            uItemDto.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            uItemDto.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            uItemDto.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            uItemDto.setRequestId(itemDto.getRequestId());
        }
        final Item uItem = ItemMapper.toItem(uItemDto);
        uItem.setOwner(UserMapper.toUser(userDto));
        return ItemMapper.toItemDto(itemRepository.updateItem(itemId, uItem));

    }

    @Override
    public void deleteItem(Long itemId) {
        final ItemDto uItemDto = getItemByIdWithCheckNull(itemId);
        itemRepository.deleteItem(itemId);
    }

    @Override
    public List<ItemDto> searchItemsByNameByDirector(String text) {
        return itemRepository.searchItemsByNameByDirector(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private ItemDto getItemByIdWithCheckNull(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Идентификатор вещи не может быть равен null");
        }
        final ItemDto resItem = ItemMapper.toItemDto(itemRepository.getItemById(itemId));
        if (resItem == null) {
            throw new NotFoundException("Вещь с id=" + itemId + " не найден");
        }
        return resItem;
    }

    private UserDto getUserByIdWithCheckNull(Long userId) {
        if (userId == null) {
            throw new ValidationException("Идентификатор пользователя не может быть равен null");
        }
        final User resUser = userRepository.getUserById(userId);
        if (resUser == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return UserMapper.toUserDto(resUser);
    }
}
