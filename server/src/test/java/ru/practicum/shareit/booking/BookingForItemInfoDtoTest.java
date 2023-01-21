package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingForItemInfoDto;
import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class BookingForItemInfoDtoTest {

    @Autowired
    private JacksonTester<BookingForItemInfoDto> json;

    @Test
    void testSerialize() throws Exception {
        final BookingForItemInfoDto bookingForItemInfoDto = new BookingForItemInfoDto(1L, 1L);

        JsonContent<BookingForItemInfoDto> result = json.write(bookingForItemInfoDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingForItemInfoDto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(bookingForItemInfoDto.getBookerId().intValue());
    }
}