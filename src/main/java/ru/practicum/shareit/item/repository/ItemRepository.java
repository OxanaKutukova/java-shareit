package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Component
public interface ItemRepository {
    Item createItem(Item item);

    Collection<Item> getAllItems();

    Item updateItem(Long itemId, Item item);

    void deleteItem(Long itemId);

    List<Item> searchItemsByNameByDirector(String text);

    Item getItemById(Long itemId);
}
