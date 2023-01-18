package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.service.BookingService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@AllArgsConstructor
@RestController
@Validated
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    //Добавить новый запрос на бронирование
   @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody BookingInDto bookingInDto) {
        log.info("createBooking (POST /bookings/): Добавить новый запрос на бронирование (входной параметр) = {} для пользователя {}", bookingInDto, userId);
        BookingDto resBookingDto = bookingService.create(userId, bookingInDto);
        log.info("createBooking (POST /bookings/): Запрос на бронирование добавлен: {}", resBookingDto);

        return resBookingDto;
    }

    //Подтверждение или отклонение запроса на бронирование
    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId,
                              @RequestParam Boolean approved) {
        log.info("approveBooking (PATCH /bookings/{}?approved={}): Запрос = {} для пользователя {} подтвердить {}",
                bookingId, approved, bookingId, userId, approved);
        BookingDto resBookingDto = bookingService.approve(userId, bookingId, approved);
        log.info("approveBooking (POST /bookings/): Запрос на бронирование подтвержден или отклонен: {}", resBookingDto);

        return resBookingDto;
    }

    //Получить бронирование по Id
    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        log.info("getBookingById (GET /bookings/{}): Получить бронирование по id(входной параметр) = {}", bookingId, bookingId);
        BookingDto bookingDto = bookingService.getById(userId,bookingId);
        log.info("getBookingById (GET /bookings/{}): Результат = {}", bookingId, bookingDto);

        return bookingDto;
    }

    // Получение списка всех бронирований текущего пользователя
    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "25") Integer size) {
        log.info("getAllByBooker (GET /bookings?state={}): Получить список всех бронирований со статусом: {} " +
                "текущего пользователя (входной параметр) = {}", state, state, userId);
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<BookingDto> allBookings = bookingService.getAllByBooker(userId, state, pageable);
        log.info("getAllByBooker (GET /bookings?state={}): Результат = {}", state, allBookings);

        return allBookings;
    }

    //Получение списка бронирований для всех вещей текущего пользователя
    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "ALL") String state,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "25") Integer size
                                          ) {
        log.info("getAllByOwner (GET /bookings/owner?state={}): Получить список бронирований со статусом: {} " +
                " для всех вещей текущего пользователя (входной параметр) = {}", state, state, userId);
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<BookingDto> allBookings = bookingService.getAllByOwner(userId, state, pageable);
        log.info("getAllByOwner (GET /bookings/owner?state={}): Результат = {}", state, allBookings);

        return allBookings;
    }
}
