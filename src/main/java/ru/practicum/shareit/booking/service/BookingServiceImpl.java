package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User getUserById(Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return user;
    }

    private Item getItemById(Long itemId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найден"));

        return item;
    }

    private Booking getBookingById(Long bookingId) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));

        return booking;
    }

    @Transactional
    @Override
    public BookingDto create(Long userId, BookingInDto bookingInDto) {
        final Booking booking = BookingMapper.fromBookingInToBooking(bookingInDto);
        //Проверим пользователя
        final User user = getUserById(userId);
        //Проверим вещь
        final Item item = getItemById(bookingInDto.getItemId());
        //Проверим, чтобы время начала < времени окончания бронирования
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new BadRequestException("Время начала бронирования должно быть меньше времени окончания бронирования!");
        }
        //Проверим доступность вещи для бронирования
        if (!item.getAvailable()) {
            throw new BadRequestException("Данная вещь недоступна для бронирования");
        }
        //Проверим владельца вещи и того, кто бронирует
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владельцу вещи не доступно бронирование у самого себя");
        }
        booking.setBooker(user);
        booking.setItem(item);
        final Booking bookingS = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(bookingS);
    }

    @Transactional
    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        //Проверим бронирование
        final Booking booking = getBookingById(bookingId);
        //Проверим значение присланного параметра approved
        if (approved == null) {
            throw new BadRequestException("Параметр подтверждения бронирования должен иметь значение либо true, либо false");
        }
        //Проверим кто подтверждает бронирование
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Подтвердить бронирование может только владелец вещи");
        }
        //Проверим подтверждено ли бронирование
        if (booking.getStatus().equals(BookingState.APPROVED)) {
            throw new BadRequestException("Бронирование уже подтверждено");
        }
        if (approved) {
            booking.setStatus(BookingState.APPROVED);
        } else {
            booking.setStatus(BookingState.REJECTED);
        }
        final Booking bookingU = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(bookingU);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        //Проверим бронирование
        final Booking booking = getBookingById(bookingId);

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Запросить информацию о бронировании может либо владелец вещи либо автор бронирования");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBooker(Long userId, String state) {
        //Проверим пользователя
        final User user = getUserById(userId);
        try {
            BookingState bookingState = BookingState.valueOf(state);
            switch (bookingState) {
                case ALL:
                    return bookingRepository.findAllByBooker_Id(userId, Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                                    LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(),
                                    Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());

                case WAITING:
                case REJECTED:
                    return bookingRepository.findAllByBooker_IdAndStatus(userId, bookingState, Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                default:
                    throw new BadRequestException("UNSUPPORTED STATUS");

            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state) {
        //Проверим пользователя
        final User user = getUserById(userId);
        try {
            BookingState bookingState = BookingState.valueOf(state);
            switch (bookingState) {
                case ALL:
                    return bookingRepository.findAllByItem_Owner_Id(userId, Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                                    LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(),
                                    Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());

                case WAITING:
                case REJECTED:
                    return bookingRepository.findAllByItem_Owner_IdAndStatus(userId, BookingState.valueOf(state), Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                default:
                    throw new BadRequestException("UNSUPPORTED STATUS");

            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }
}
