package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	//Добавить новый запрос на бронирование
	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
							 @Valid @RequestBody BookingInDto bookingInDto) {
		log.info("Gateway: Добавлен запрос на бронирование = {}", bookingInDto);

		return bookingClient.create(userId, bookingInDto);
	}

	//Подтверждение или отклонение запроса на бронирование
	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Long userId,
							  @PathVariable Long bookingId,
							  @RequestParam Boolean approved) {
		log.info("Gateway: Запрос на бронирование c id {} подтвержден/отклонен: {}", bookingId, approved);

		return bookingClient.approve(userId, bookingId, approved);
	}

	//Получить бронирование по Id
	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
							  @PathVariable Long bookingId) {
		log.info("Gateway: Получено бронирование id = {} ", bookingId);

		return bookingClient.getById(userId, bookingId);
	}

	// Получение списка всех бронирований текущего пользователя
	@GetMapping
	public ResponseEntity<Object> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
										   @RequestParam(defaultValue = "ALL") String state,
										   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
										   @Positive @RequestParam(name = "size", defaultValue = "25") Integer size) {
		log.info("Gateway: Получен список всех бронирований со статусом: {}, " +
				"для пользователя с id = {} ", state, userId);

		return bookingClient.getAlByBooker(userId, state, from, size);
	}

	//Получение списка бронирований для всех вещей текущего пользователя
	@GetMapping("/owner")
	public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
										  @RequestParam(defaultValue = "ALL") String state,
										  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
										  @Positive @RequestParam(name = "size", defaultValue = "25") Integer size
	) {
		log.info("Gateway: Получен список всех бронирований вещей пользователя с id= {} со статусом: {} ", userId, state);

		return bookingClient.getAllByOwner(userId, state, from, size);
	}
}
