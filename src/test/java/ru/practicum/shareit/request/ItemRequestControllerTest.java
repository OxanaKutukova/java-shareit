package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({ItemRequestController.class})
class ItemRequestControllerTest {
    private static final String PATH = "/requests";
    private static final String PATH_WITH_ID = "/requests/1";
    private static final String PATH_ALL = "/requests/all";
    private static final Long USER_ID = 1L;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    private ItemRequestDto itemRequestDto;
    private ItemRequestInfoDto itemRequestInfoDto;
    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        itemRequestDto = new ItemRequestDto(1L, "ItemRequestDescription", dateTime);

        itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви", true, 1L,1L);
        itemRequestInfoDto = new ItemRequestInfoDto(1L, "Нужна щетка для обуви", dateTime, List.of(itemDto));
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.create(any(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", USER_ID)
                        .content(getContentFromFile("itemRequest/request/create.json"))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("itemRequest/response/create.json")));
    }

    @Test
    void getAll() throws Exception {
        when(itemRequestService.getAll(any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id", USER_ID)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getById() throws Exception {
        when(itemRequestService.getById(any(), any()))
                .thenReturn(itemRequestInfoDto);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                .header("X-Sharer-User-Id", USER_ID)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("itemRequest/response/getById.json")));
    }

    @Test
    void getAllWithPage() throws Exception {
        List<ItemRequestInfoDto> allItemRequestsInfoDto = new ArrayList<>();
        allItemRequestsInfoDto.add(itemRequestInfoDto);
        when(itemRequestService.getAllWithPage(any(), any()))
                .thenReturn(allItemRequestsInfoDto);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_ALL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("from", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("itemRequest/response/getAllWithPage.json")));
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