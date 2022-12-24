package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Component
public interface ItemRepository {


    List<Item> findAll();

    Optional<Item> findById(Long itemId);

    Item create(Item item);

    Item update(Long itemId, Item item);

    void delete(Long itemId);

    List<Item> findByNameByDirector(String text);

    List<Item> findByOwnerId(Long userId);
}
