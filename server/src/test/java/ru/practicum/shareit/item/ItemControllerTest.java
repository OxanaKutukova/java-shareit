package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;
import ru.practicum.shareit.booking.dto.BookingForItemInfoDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({ItemController.class})
class ItemControllerTest {
    private static final String PATH = "/items";
    private static final String PATH_WITH_ID = "/items/1";
    private static final String PATH_SEARCH = "/items/search";
    private static final String PATH_COMMENT = "/items/1/comment";
    private static final Long ITEM_ID = 1L;
    private static final Long USER_ID = 1L;
    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void getByNameByDirector() throws Exception {
        final ItemDto itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви", true, 1L, null);
        List<ItemDto> result = new ArrayList<>();
        result.add(itemDto);
        when(itemService.getByNameByDirector(any(), any()))
                .thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_SEARCH)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("from", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("item/response/getByNameByDirector.json")));
    }

    @Test
    void getByOwnerId() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final User user2 = new User(2L, "Max", "maxi.dto@ya.ru");
        final ItemRequest itemRequest = new ItemRequest(1L, "ItemRequestDescription", user2, dateTime);
        final Item item = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, user, itemRequest);
        final BookingForItemInfoDto lastBooking = new BookingForItemInfoDto(1L, 3L);
        final BookingForItemInfoDto nextBooking = new BookingForItemInfoDto(2L, 25L);
        ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item, lastBooking, nextBooking);
        List<ItemInfoDto> result = new ArrayList<>();
        result.add(itemInfoDto);
        when(itemService.getByOwnerId(any(), any()))
                .thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("from", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("item/response/getByOwnerId.json")));
    }

    @Test
    void getById() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final User user2 = new User(2L, "Max", "maxi.dto@ya.ru");
        final ItemRequest itemRequest = new ItemRequest(1L, "ItemRequestDescription", user2, dateTime);
        final Item item = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, user, itemRequest);
        final BookingForItemInfoDto lastBooking = new BookingForItemInfoDto(1L, 3L);
        final BookingForItemInfoDto nextBooking = new BookingForItemInfoDto(2L, 25L);
        ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item, lastBooking, nextBooking);
        when(itemService.getById(any(), any()))
                .thenReturn(itemInfoDto);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("from", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("item/response/getById.json")));
    }

    @Test
    void create() throws Exception {
        final ItemDto itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви", true, 1L, null);
        when(itemService.create(any(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", USER_ID)
                        .content(getContentFromFile("item/request/create.json"))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("item/response/create.json")));
    }

    @Test
    void update() throws Exception {
        final ItemDto itemDto = new ItemDto(1L, "Крем для обуви", "Стандартный крем для обуви", true, 1L, null);
        when(itemService.update(any(), any(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", USER_ID)
                        .content(getContentFromFile("item/request/update.json"))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("item/response/update.json")));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(PATH_WITH_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                )
                .andExpect(status().isOk());
        verify(itemService).delete(ITEM_ID);
    }

    @Test
    void createComment() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        CommentDto commentDto = new CommentDto(1L, "Best comment", "Oksi",dateTime);
        when(itemService.createComment(any(), any(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH_COMMENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", USER_ID)

                        .content(getContentFromFile("item/request/createComment.json"))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("item/response/createComment.json")));
    }

    private String getContentFromFile(final String fileName) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + fileName).toPath(),
                    StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            throw new RuntimeException("Unable to open file", exception);
        }
    }

}