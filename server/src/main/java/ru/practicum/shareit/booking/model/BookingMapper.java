package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemInfoDto;
import ru.practicum.shareit.booking.dto.BookingInDto;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto
                .builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking
                .builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(bookingDto.getItem())
                .booker(bookingDto.getBooker())
                .status(bookingDto.getStatus())
                .build();
    }

    public static Booking fromBookingInToBooking(BookingInDto bookingInDto) {
        return Booking.builder()
                .id(bookingInDto.getId())
                .start(bookingInDto.getStart())
                .end(bookingInDto.getEnd())
                .status(BookingState.WAITING)
                .build();
    }

    public static BookingForItemInfoDto toBookingForItemInfo(Booking booking) {
        return BookingForItemInfoDto
                .builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
