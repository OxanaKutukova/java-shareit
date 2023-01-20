package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.booking.dto.BookingForItemInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest({ItemMapper.class})
class ItemMapperTest {

    @Test
    void toItemDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final User user2 = new User(2L, "Max", "maxi.dto@ya.ru");
        final ItemRequest itemRequest = new ItemRequest(1L, "ItemRequestDescription", user2, dateTime);
        final Item item = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, user, itemRequest);

        ItemDto result = ItemMapper.toItemDto(item);

        Assertions.assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getOwner().getId(), result.getOwnerId());
    }

    @Test
    void toItem() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final ItemDto itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, user.getId(), null);

        Item result = ItemMapper.toItem(itemDto, user);

        Assertions.assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(user, result.getOwner());
    }

    @Test
    void toItemInfoDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final User user2 = new User(2L, "Max", "maxi.dto@ya.ru");
        final ItemRequest itemRequest = new ItemRequest(1L, "ItemRequestDescription", user2, dateTime);
        final Item item = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, user, itemRequest);
        final BookingForItemInfoDto lastBooking = new BookingForItemInfoDto(1L, 3L);
        final BookingForItemInfoDto nextBooking = new BookingForItemInfoDto(2L, 25L);


        ItemInfoDto result = ItemMapper.toItemInfoDto(item, lastBooking, nextBooking);

        Assertions.assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(lastBooking, result.getLastBooking());
        assertEquals(nextBooking, result.getNextBooking());
    }
}