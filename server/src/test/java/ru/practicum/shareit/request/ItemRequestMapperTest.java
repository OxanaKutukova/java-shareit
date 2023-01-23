package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest({ItemRequestMapper.class})
class ItemRequestMapperTest {

    @Test
    void toItemRequest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "ItemRequestDescription", dateTime);

        ItemRequest result  = ItemRequestMapper.toItemRequest(itemRequestDto, user);

        Assertions.assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(user.getId(), result.getRequestor().getId());
        assertEquals(user.getName(), result.getRequestor().getName());
        assertEquals(user.getEmail(), result.getRequestor().getEmail());
    }

    @Test
    void toItemRequestDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final ItemRequest itemRequest = new ItemRequest(1L, "ItemRequestDescription", user, dateTime);

        ItemRequestDto result  = ItemRequestMapper.toItemRequestDto(itemRequest);

        Assertions.assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());
    }

    @Test
    void toItemRequestInfoDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final ItemRequest itemRequest = new ItemRequest(1L, "ItemRequestDescription", user, dateTime);

        ItemRequestInfoDto result  = ItemRequestMapper.toItemRequestInfoDto(itemRequest);

        Assertions.assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());
    }
}