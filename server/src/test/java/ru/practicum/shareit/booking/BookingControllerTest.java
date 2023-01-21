package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@WebMvcTest({BookingController.class})
class BookingControllerTest {

    private static final String PATH = "/bookings";
    private static final String PATH_WITH_ID = "/bookings/1";
    private static final String PATH_WITH_OWNER = "/bookings/owner";
    private static final Long USER_ID = 1L;
    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void create() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("2023-03-10 10:15:30", formatter);
        final User owner = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final User booker = new User(2L, "Max", "maxi.dto@ya.ru");
        final ItemDto itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, owner.getId(), null);
        final BookingDto bookingDto = new BookingDto(1L, startDateTime, endDateTime, ItemMapper.toItem(itemDto, owner),
                booker, BookingState.WAITING);
        when(bookingService.create(any(), any()))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", USER_ID)
                        .content(getContentFromFile("booking/request/create.json"))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("booking/response/create.json")));
    }

    @Test
    void approve() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("2023-03-10 10:15:30", formatter);
        final User owner = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final User booker = new User(2L, "Max", "maxi.dto@ya.ru");
        final ItemDto itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, owner.getId(), null);
        final BookingDto bookingDto = new BookingDto(1L, startDateTime, endDateTime, ItemMapper.toItem(itemDto, owner),
                booker, BookingState.APPROVED);
        when(bookingService.approve(any(), any(), any()))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("approved", "true")
                        .content(getContentFromFile("booking/request/approve.json"))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("booking/response/approve.json")));
    }

    @Test
    void getById() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("2023-03-10 10:15:30", formatter);
        final User owner = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final User booker = new User(2L, "Max", "maxi.dto@ya.ru");
        final ItemDto itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, owner.getId(), null);
        final BookingDto bookingDto = new BookingDto(1L, startDateTime, endDateTime, ItemMapper.toItem(itemDto, owner),
                booker, BookingState.WAITING);
        when(bookingService.getById(any(), any()))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("booking/response/getById.json")));
    }

    @Test
    void getAllByBooker() throws Exception {
        when(bookingService.getAllByBooker(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("from", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllByOwner() throws Exception {
        when(bookingService.getAllByOwner(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_OWNER)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("from", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
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