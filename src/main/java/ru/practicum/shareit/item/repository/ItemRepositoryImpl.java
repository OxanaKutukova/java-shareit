package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long generateItemId = 0L;

    @Override
    public Item createItem(Item item) {
        item.setId(++generateItemId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> getAllItems() {
        return items.values();
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        items.put(itemId, item);
        return item;
    }

    @Override
    public void deleteItem(Long itemId) {

        items.remove(itemId);
    }

    @Override
    public List<Item> searchItemsByNameByDirector(String text) {
        final String searchText = text.toLowerCase();

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        Predicate<Item> inName = item -> item.getName().toLowerCase().contains(searchText);
        Predicate<Item> inDesc = item -> item.getDescription().toLowerCase().contains(searchText);

        return getAllItems()
                .stream()
                .filter(inName.or(inDesc))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long itemId) {

        return items.get(itemId);
    }
}
