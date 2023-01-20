package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;
    private User owner1;
    private User owner2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void beforeEach() {
        owner1 = userRepository.save(new User(1L, "Oksi", "oksi.dto@ya.ru"));
        item1 = itemRepository.save(new Item(1L, "Щётка для обуви",
                "Стандартная щётка для обуви", true, owner1, null));
        owner2 = userRepository.save(new User(2L, "Max", "maxi.dto@ya.ru"));
        item2 = itemRepository.save(new Item(2L, "Робот-щётка для обуви",
                "Программируемый робот", true, owner2, null));

    }

    @Test
    void search_ReturnOneItem() {
        final List<Item> result = itemRepository.search("Робот", Pageable.unpaged());

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item2, result.get(0));
    }

    @Test
    void search_ReturnTwoItems() {
        final List<Item> result = itemRepository.search("Щётка", Pageable.unpaged());

        Assertions.assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findByOwnerId() {
        final List<Item> result = itemRepository.findByOwnerId(owner1.getId(), Pageable.unpaged());

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1, result.get(0));
    }

    @Test
    void findAllByItemRequest_Id() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final ItemRequest itemRequest = itemRequestRepository.save(new ItemRequest(1L,
                "Нужна щетка для обуви", owner2, dateTime));
        item1.setItemRequest(itemRequest);

        final List<Item> result = itemRepository.findAllByItemRequest_Id(1L);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1, result.get(0));
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}