package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingInDto bookingInDto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByBooker(Long userId, String state);

    List<BookingDto> getAllByOwner(Long userId, String state);
}
