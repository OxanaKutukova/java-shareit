package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
class ItemRequestRepositoryTest {
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
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;


    @BeforeEach
    void beforeEach() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
/*        owner1 = userRepository.save(new User(1L, "Oksi", "oksi.dto@ya.ru"));
        item1 = itemRepository.save(new Item(1L, "Щётка для обуви",
                "Стандартная щётка для обуви", true, owner1, null));
        owner2 = userRepository.save(new User(2L, "Max", "maxi.dto@ya.ru"));
        item2 = itemRepository.save(new Item(2L, "Робот-щётка для обуви",
                "Программируемый робот", true, owner2, null));
        itemRequest1 = itemRequestRepository.save(new ItemRequest(1L,
                "Нужна щетка для обуви", owner2, dateTime));
        itemRequest2 = itemRequestRepository.save(new ItemRequest(2L,
                "Очень нужен робот чистить обувь", owner1, dateTime));
*/
        owner1 = userRepository.save(
                User
                        .builder()
                        .id(1L)
                        .name("Oksi")
                        .email("oksi.dto@ya.ru")
                        .build()
        );

        owner2 = userRepository.save(
                User
                        .builder()
                        .id(2L)
                        .name("Max")
                        .email("maxi.dto@ya.ru")
                        .build()
        );
        item1 = itemRepository.save(
                Item
                        .builder()
                        .id(1L)
                        .name("Щётка для обуви")
                        .description("Стандартная щётка для обуви")
                        .available(true)
                        .owner(owner1)
                        .build()
        );

        item1 = itemRepository.save(
                Item
                        .builder()
                        .id(2L)
                        .name("Щётка для обуви")
                        .description("Программируемый робот")
                        .available(true)
                        .owner(owner2)
                        .build()
        );
        itemRequest1 = itemRequestRepository.save(
                ItemRequest
                        .builder()
                        .id(1L)
                        .description("Нужна щетка для обуви")
                        .requestor(owner2)
                        .created(dateTime)
                        .build()
        );
        itemRequest2 = itemRequestRepository.save(
                ItemRequest
                        .builder()
                        .id(2L)
                        .description("Очень нужен робот, чистить обувь")
                        .requestor(owner1)
                        .created(dateTime)
                        .build()
        );
    }

    @Test
    void findAllByRequestor_Id() {
        final List<ItemRequest> result = itemRequestRepository.findAllByRequestor_Id(owner1.getId(),
                Sort.by(Sort.Direction.DESC, "created"));

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequest2, result.get(0));
    }

    @Test
    void findAllByNotRequestorId() {
        final List<ItemRequest> result = itemRequestRepository.findAllByNotRequestorId(owner2.getId(),
                Pageable.unpaged());

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequest2, result.get(0));
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}