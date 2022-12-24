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
    public List<Item> findAll() {
        return new ArrayList<Item>(items.values());
    }

    @Override
    public Optional<Item> findById(Long itemId) {

        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item create(Item item) {
        item.setId(++generateItemId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long itemId, Item item) {
        items.put(itemId, item);

        return item;
    }

    @Override
    public void delete(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> findByNameByDirector(String text) {
        final String searchText = text.toLowerCase();

        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Predicate<Item> inName = item -> item.getName().toLowerCase().contains(searchText);
        Predicate<Item> inDesc = item -> item.getDescription().toLowerCase().contains(searchText);

        return findAll()
                .stream()
                .filter(inName.or(inDesc))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }



    @Override
    public List<Item> findByOwnerId(Long userId) {
        return   findAll()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }
}
