package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInDto;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class BookingInDtoTest {

    @Autowired
    private JacksonTester<BookingInDto> json;

    @Test
    void testSerialize() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("2023-03-10 10:15:30", formatter);

        final BookingInDto bookingInDto = new BookingInDto(1L, startDateTime, endDateTime,1L);

        JsonContent<BookingInDto> result = json.write(bookingInDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingInDto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(bookingInDto.getItemId().intValue());
    }

}