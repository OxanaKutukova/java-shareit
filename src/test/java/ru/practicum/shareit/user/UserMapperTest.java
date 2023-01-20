package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest({UserMapper.class})
class UserMapperTest {

    @Test
    void toUserDto() {
        User user = new User(1L, "Oksi", "oksi.dto@ya.ru");

        UserDto result = UserMapper.toUserDto(user);

        Assertions.assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void toUser() {
        UserDto userDto = new UserDto(1L, "Oksi", "oksi.dto@ya.ru");

        User result = UserMapper.toUser(userDto);

        Assertions.assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());

    }
}