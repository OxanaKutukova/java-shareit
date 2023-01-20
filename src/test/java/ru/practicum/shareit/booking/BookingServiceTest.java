package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;

class BookingServiceTest {
    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private  UserRepository userRepository;
    private DateTimeFormatter formatter;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private BookingInDto bookingInDto;
    private User owner;
    private User booker;
    private ItemDto itemDto;
    private Booking booking;
    private PageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        startDateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        endDateTime = LocalDateTime.parse("2023-03-10 10:15:30", formatter);
        bookingInDto = new BookingInDto(1L, startDateTime, endDateTime, 1L);
        owner = new User(1L, "Oksi", "oksi.dto@ya.ru");
        booker = new User(2L, "Max", "maxi.dto@ya.ru");
        itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, owner.getId(), null);
        booking = new Booking(1L, startDateTime, endDateTime, ItemMapper.toItem(itemDto, owner),
                booker, BookingState.WAITING);
        pageRequest = PageRequest.of(0 / 20, 20, Sort.by(Sort.Direction.DESC, "created"));

    }

    @Test
    void create() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(ItemMapper.toItem(itemDto, owner)));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto result = bookingService.create(booker.getId(), bookingInDto);

        Assertions.assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getItem(), result.getItem());
        assertEquals(booking.getBooker(), result.getBooker());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void create_StartDateGTEndDate() {
        booking.setStart(LocalDateTime.parse("2023-03-10 10:15:30", formatter));
        booking.setEnd(LocalDateTime.parse("2023-03-01 10:15:30", formatter));
        bookingInDto.setStart(LocalDateTime.parse("2023-03-10 10:15:30", formatter));
        bookingInDto.setEnd(LocalDateTime.parse("2023-03-01 10:15:30", formatter));

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(ItemMapper.toItem(itemDto,owner)));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        Throwable throwable = assertThrows(BadRequestException.class, () -> bookingService.create(booker.getId(), bookingInDto));

        assertEquals("Время начала бронирования должно быть меньше времени окончания бронирования!", throwable.getMessage(),
                "Время начала бронирования больше времени окончания бронирования");
    }

    @Test
    void create_CheckItemAvail() {
        itemDto.setAvailable(false);
        booking.getItem().setAvailable(false);
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(ItemMapper.toItem(itemDto,owner)));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        Throwable throwable = assertThrows(BadRequestException.class, () -> bookingService.create(booker.getId(), bookingInDto));

        assertEquals("Данная вещь недоступна для бронирования", throwable.getMessage(),
                "Данная вещь недоступна для бронирования");
    }

    @Test
    void create_CheckOwnerEqBooker() {
        booking.setBooker(owner);
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(ItemMapper.toItem(itemDto,owner)));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.create(owner.getId(), bookingInDto));

        assertEquals("Владельцу вещи не доступно бронирование у самого себя", throwable.getMessage(),
                "Владельцу вещи не доступно бронирование у самого себя");
    }

    @Test
    void approve() {
        final Booking bookingApp = new Booking(1L, startDateTime, endDateTime, ItemMapper.toItem(itemDto, owner),
                booker, BookingState.APPROVED);

        when(bookingRepository.findById(bookingInDto.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenReturn(bookingApp);

        BookingDto result = bookingService.approve(owner.getId(),bookingInDto.getId(),true);

        Assertions.assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getItem(), result.getItem());
        assertEquals(booking.getBooker(), result.getBooker());
        assertEquals(BookingState.APPROVED, result.getStatus());
    }

    @Test
    void approve_DontApproved() {
        final Booking bookingApp = new Booking(1L, startDateTime, endDateTime, ItemMapper.toItem(itemDto, owner),
                booker, BookingState.REJECTED);

        when(bookingRepository.findById(bookingInDto.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenReturn(bookingApp);

        BookingDto result = bookingService.approve(owner.getId(),bookingInDto.getId(),false);

        Assertions.assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getItem(), result.getItem());
        assertEquals(booking.getBooker(), result.getBooker());
        assertEquals(BookingState.REJECTED, result.getStatus());
    }

    @Test
    void approve_CheckOwner() {
        when(bookingRepository.findById(bookingInDto.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.approve(booker.getId(),bookingInDto.getId(),true));

        assertEquals("Подтвердить бронирование может только владелец вещи", throwable.getMessage(),
                "Подтвердить бронирование может только владелец вещи");
    }

    @Test
    void approve_CheckState() {
        booking.setStatus(BookingState.APPROVED);
        when(bookingRepository.findById(bookingInDto.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        Throwable throwable = assertThrows(BadRequestException.class, () -> bookingService.approve(owner.getId(),bookingInDto.getId(),true));

        assertEquals("Бронирование уже подтверждено", throwable.getMessage(),
                "Бронирование уже подтверждено");
    }

    @Test
    public void getById() {
        when(bookingRepository.findById(bookingInDto.getId()))
                .thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getById(booker.getId(), bookingInDto.getId());

        Assertions.assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getItem(), result.getItem());
        assertEquals(booking.getBooker(), result.getBooker());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    public void getById_UnknownUserId() {

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.getById(1L,100L));

        assertEquals("Бронирование с id=100 не найдено", throwable.getMessage(),
                "Неверный идентификатор бронирования");
    }

    @Test
    public void getById_IncorrectUser() {
        when(bookingRepository.findById(bookingInDto.getId()))
                .thenReturn(Optional.of(booking));

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.getById(100L,1L));

        assertEquals("Запросить информацию о бронировании может либо владелец вещи либо автор бронирования", throwable.getMessage(),
                "Запросить информацию о бронировании может либо владелец вещи либо автор бронирования");
    }

    @Test
    public void getAllByBooker_StateEqAll() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));

        when(bookingRepository.findAllByBooker_Id(booker.getId(), pageRequest))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.ALL.toString(), pageRequest);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    public void getAllByBooker_StateEqCurrent() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));

        when(bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.CURRENT.toString(), pageRequest);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    public void getAllByBooker_StateEqPast() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));

        when(bookingRepository.findAllByBooker_IdAndEndIsBefore(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.PAST.toString(), pageRequest);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    public void getAllByBooker_StateEqFuture() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));

        when(bookingRepository.findAllByBooker_IdAndStartIsAfter(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.FUTURE.toString(), pageRequest);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    public void getAllByBooker_StateEqWaiting() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));

        when(bookingRepository.findAllByBooker_IdAndStatus(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.WAITING.toString(), pageRequest);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    public void getAllByBooker_IncorrectState() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));

        Throwable throwable = assertThrows(BadRequestException.class, () -> bookingService.getAllByBooker(booker.getId(), "ANY", pageRequest));

        assertEquals("Unknown state: ANY", throwable.getMessage(),
                "Передан некорректный статус");
    }

    @Test
    public void getAllByOwner_StateEqAll() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findAllByItem_Owner_Id(owner.getId(), pageRequest))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.ALL.toString(), pageRequest);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    public void getAllByOwner_StateEqCurrent() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.CURRENT.toString(), pageRequest);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    public void getAllByOwner_StateEqPast() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.PAST.toString(), pageRequest);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    public void getAllByOwner_StateEqFuture() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.FUTURE.toString(), pageRequest);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    public void getAllByOwner_StateEqWaiting() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findAllByItem_Owner_IdAndStatus(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.WAITING.toString(), pageRequest);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    public void getAllByOwner_IncorrectState() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        Throwable throwable = assertThrows(BadRequestException.class, () -> bookingService.getAllByOwner(owner.getId(), "ANY", pageRequest));

        assertEquals("Unknown state: ANY", throwable.getMessage(),
                "Передан некорректный статус");
    }
}