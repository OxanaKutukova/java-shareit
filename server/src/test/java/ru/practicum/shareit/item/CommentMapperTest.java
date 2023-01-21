package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest({CommentMapper.class})
class CommentMapperTest {


    @Test
    void toCommentDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final Item item = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, user, null);
       final Comment comment = new Comment(1L, "Best comment", item, user, dateTime);

        CommentDto result = CommentMapper.toCommentDto(comment);

        Assertions.assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getCreated(), result.getCreated());
    }

    @Test
    void toComment() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final Item item = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, user, null);
        final CommentDto commentDto = new CommentDto(1L, "Best comment", "Oksi", dateTime);

        Comment result = CommentMapper.toComment(commentDto, user, item);

        Assertions.assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());
        assertEquals(commentDto.getText(), result.getText());
    }

}