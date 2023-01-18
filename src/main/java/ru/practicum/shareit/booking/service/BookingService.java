package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingInDto bookingInDto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByBooker(Long userId, String state, Pageable pageable);

    List<BookingDto> getAllByOwner(Long userId, String state, Pageable pageable);
}
