package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingForItemInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemInfoDtoTest {

    @Autowired
    private JacksonTester<ItemInfoDto> json;

    @Test
    void testSerialize() throws Exception {
        final BookingForItemInfoDto lastBooking = new BookingForItemInfoDto(1L, 3L);
        final BookingForItemInfoDto nextBooking = new BookingForItemInfoDto(2L, 25L);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        CommentDto commentDto = new CommentDto(1L, "Best comment", "Oksi",dateTime);
        final List<CommentDto> comments = new ArrayList<>();
        comments.add(commentDto);

        ItemInfoDto itemInfoDto = new ItemInfoDto(1L, "Щётка для обуви", "Стандартная щётка для обуви", true,
                lastBooking, nextBooking, comments);

        JsonContent<ItemInfoDto> result = json.write(itemInfoDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemInfoDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemInfoDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemInfoDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemInfoDto.getAvailable());
    }

}