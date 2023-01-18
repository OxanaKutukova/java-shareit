package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({UserController.class})
class UserControllerTest {

    private static final String PATH = "/users";
    private static final String PATH_WITH_ID = "/users/1";
    private static final Long USER_ID = 1L;
    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void getAll() throws Exception {
        when(userService.getAll())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getById() throws Exception {
        UserDto userDto = new UserDto(1L, "Oksi", "oksi.dto@ya.ru");
        when(userService.getById(USER_ID))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("user/response/getById.json")));
    }

    @Test
    void create() throws Exception {
        final UserDto userDto = new UserDto(1L, "Oksi", "oksi.dto@ya.ru");
        when(userService.create(any()))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContentFromFile("user/request/create.json"))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("user/response/create.json")));
    }

    @Test
    void update() throws Exception {
        final UserDto userDto = new UserDto(1L, "Max", "maxi.dto@ya.ru");
        when(userService.update(any(), any()))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("user/request/update.json"))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(getContentFromFile("user/response/update.json")));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(PATH_WITH_ID))
                .andExpect(status().isOk());
        verify(userService).delete(USER_ID);
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