package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemInfoDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest({BookingMapper.class})
class BookingMapperTest {

    @Test
    void toBooking() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("2023-03-10 10:15:30", formatter);
        final User owner = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final User booker = new User(2L, "Max", "maxi.dto@ya.ru");
        final ItemDto itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, owner.getId(), null);
        final BookingDto bookingDto = new BookingDto(1L, startDateTime, endDateTime, ItemMapper.toItem(itemDto, owner),
                booker, BookingState.WAITING);

        Booking result = BookingMapper.toBooking(bookingDto);

        Assertions.assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(bookingDto.getStatus(), result.getStatus());
        assertEquals(bookingDto.getItem(), result.getItem());
        assertEquals(bookingDto.getBooker(), result.getBooker());
    }

    @Test
    void toBookingDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("2023-03-10 10:15:30", formatter);
        final User owner = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final User booker = new User(2L, "Max", "maxi.dto@ya.ru");
        final ItemDto itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, owner.getId(), null);
        final Booking booking = new Booking(1L, startDateTime, endDateTime, ItemMapper.toItem(itemDto, owner),
                booker, BookingState.WAITING);

        BookingDto result = BookingMapper.toBookingDto(booking);

        Assertions.assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(booking.getItem(), result.getItem());
        assertEquals(booking.getBooker(), result.getBooker());
    }

    @Test
    void fromBookingInToBooking() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("2023-03-10 10:15:30", formatter);
        final BookingInDto bookingInDto = new BookingInDto(1L, startDateTime, endDateTime, 1L);

        Booking result = BookingMapper.fromBookingInToBooking(bookingInDto);

        Assertions.assertNotNull(result);
        assertEquals(bookingInDto.getId(), result.getId());
        assertEquals(bookingInDto.getStart(), result.getStart());
        assertEquals(bookingInDto.getEnd(), result.getEnd());

    }

    @Test
    void bookingForItemInfoDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("2023-03-10 10:15:30", formatter);
        final User owner = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final User booker = new User(2L, "Max", "maxi.dto@ya.ru");
        final ItemDto itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, owner.getId(), null);
        final Booking booking = new Booking(1L, startDateTime, endDateTime, ItemMapper.toItem(itemDto, owner),
                booker, BookingState.WAITING);

        BookingForItemInfoDto result = BookingMapper.toBookingForItemInfo(booking);

        Assertions.assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getBooker().getId(), result.getBookerId());
    }

}