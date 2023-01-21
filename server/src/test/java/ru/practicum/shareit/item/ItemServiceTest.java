package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class ItemServiceTest {

    private ItemService itemService;
    private  UserRepository userRepository;
    private  ItemRepository itemRepository;
    private  BookingRepository bookingRepository;
    private  CommentRepository commentRepository;
    private  ItemRequestRepository itemRequestRepository;
    private User owner;
    private User booker;
    private ItemDto itemDto;
    private Item item;
    private Pageable pageable;
    private DateTimeFormatter formatter;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Booking booking;


    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(userRepository, itemRepository, bookingRepository, commentRepository, itemRequestRepository);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        startDateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        endDateTime = LocalDateTime.parse("2023-03-10 10:15:30", formatter);
        owner = new User(1L, "Oksi", "oksi.dto@ya.ru");
        itemDto = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
                true, owner.getId(), null);
        item = ItemMapper.toItem(itemDto, owner);
        booker = new User(2L, "Max", "maxi.dto@ya.ru");
        booking = new Booking(1L, startDateTime, endDateTime, ItemMapper.toItem(itemDto, owner),
                booker, BookingState.WAITING);
        pageable = PageRequest.of(0 / 20, 20, Sort.by(Sort.Direction.DESC, "created"));

    }

    @Test
    void create() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(item))
                .thenReturn(item);

        ItemDto result = itemService.create(owner.getId(), itemDto);

        Assertions.assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getOwnerId(), result.getOwnerId());
    }

    @Test
    void update() {
        final Item itemUpdate = new Item();
        itemUpdate.setId(1L);
        itemUpdate.setOwner(owner);
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemUpdate.getId()))
                .thenReturn(Optional.of(itemUpdate));
        when(itemRepository.save(ItemMapper.toItem(itemDto, owner)))
                .thenReturn(ItemMapper.toItem(itemDto, owner));

        ItemDto result = itemService.update(owner.getId(), itemDto.getId(), itemDto);

        Assertions.assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getOwnerId(), result.getOwnerId());
    }

    @Test
    void delete() {
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(ItemMapper.toItem(itemDto, owner)));

        itemService.delete(itemDto.getId());

        verify(itemRepository, times(1)).deleteById(itemDto.getId());
    }

    @Test
    public void update_ByNotOwner() {
        final User userUpdate = new User(2L, "Max", "maxi.dto@ya.ru");
        final Item itemUpdate = new Item();
        itemUpdate.setId(1L);
        itemUpdate.setOwner(owner);
        when(userRepository.findById(userUpdate.getId()))
                .thenReturn(Optional.of(userUpdate));
        when(itemRepository.findById(itemUpdate.getId()))
                .thenReturn(Optional.of(itemUpdate));

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.update(userUpdate.getId(), itemDto.getId(), itemDto));

        assertEquals("Вещь с id=1 может изменять только владелец", throwable.getMessage(),
                "Неверный идентификатор запроса на вещь");
    }

    @Test
    public void createComment() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final LocalDateTime startDateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final LocalDateTime endDateTime = LocalDateTime.parse("2023-03-10 10:15:30", formatter);
        final User booker = new User(2L, "Max", "maxi.dto@ya.ru");
        final CommentDto commentDto = new CommentDto(1L, "Best comment", "Oksi",dateTime);
        final Booking booking = new Booking(1L, startDateTime, endDateTime, ItemMapper.toItem(itemDto, owner),
                booker, BookingState.WAITING);
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(ItemMapper.toItem(itemDto, owner)));
        when(bookingRepository.findAllByBooker_Id_AndItem_Id_AndEndIsBefore(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(CommentMapper.toComment(commentDto, owner, ItemMapper.toItem(itemDto, owner)));

        CommentDto result = itemService.createComment(owner.getId(), commentDto, itemDto.getId());

        Assertions.assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(commentDto.getAuthorName(), result.getAuthorName());
    }

    @Test
    public void createComment_WithEmptyBooking() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final CommentDto commentDto = new CommentDto(1L, "Best comment", "Oksi",dateTime);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(ItemMapper.toItem(itemDto, owner)));
        when(bookingRepository.findAllByBooker_Id_AndItem_Id_AndEndIsBefore(owner.getId(),
                                                                            itemDto.getId(),
                                                                            LocalDateTime.now(),
                                                                            Sort.by(Sort.Direction.DESC, "start")))
                .thenReturn(Collections.emptyList());

        Throwable throwable = assertThrows(BadRequestException.class, () -> itemService.createComment(owner.getId(),
                                                                                                    commentDto,
                                                                                                    itemDto.getId()));

        assertEquals("Только по бронированной вещи можно добавить комментарий", throwable.getMessage(),
                "Комментарий не по бронированной вещи");
    }

    @Test
    void getByNameByDirector_BlankText() {
        List<ItemDto> result = itemService.getByNameByDirector("", pageable);

        Assertions.assertNotNull(result);
        assertEquals(0, result.size());

    }

    @Test
    void getByNameByDirector() {

        when(itemRepository.search(any(), any()))
                .thenReturn(Collections.singletonList(ItemMapper.toItem(itemDto,owner)));

        List<ItemDto> result = itemService.getByNameByDirector("searchText", pageable);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDto, result.get(0));

    }

    @Test
    void getById() {
        final CommentDto commentDto = new CommentDto(1L, "Best comment", "Oksi", startDateTime);
        when(commentRepository.findAllByItem_Id(any(), any()))
         .thenReturn(Collections.singletonList(CommentMapper.toComment(commentDto, booker, item)));
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(ItemMapper.toItem(itemDto, owner)));
        when(bookingRepository.findFirstByItem_Owner_IdAndItemIdAndEndIsBefore(any(), any(), any(), any()))
                .thenReturn(booking);
        when(bookingRepository.findFirstByItem_Owner_IdAndItemIdAndStartIsAfter(any(), any(), any(), any()))
                .thenReturn(booking);
        when(commentRepository.findAllByItem_Id(any(), any()))
                        .thenReturn(Collections.emptyList());

        ItemInfoDto result = itemService.getById(owner.getId(), itemDto.getId());

        Assertions.assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());

    }

    @Test
    void getByOwnerId() {
        final CommentDto commentDto = new CommentDto(1L, "Best comment", "Oksi", startDateTime);
        when(commentRepository.findAllByItem_Id(any(), any()))
                .thenReturn(Collections.singletonList(CommentMapper.toComment(commentDto, booker, item)));
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(any(), any()))
                .thenReturn(Collections.singletonList(ItemMapper.toItem(itemDto, owner)));
        when(bookingRepository.findFirstByItem_Owner_IdAndItemIdAndEndIsBefore(any(), any(), any(), any()))
                .thenReturn(booking);
        when(bookingRepository.findFirstByItem_Owner_IdAndItemIdAndStartIsAfter(any(), any(), any(), any()))
                .thenReturn(booking);
        when(commentRepository.findAllByItem_Id(any(), any()))
                .thenReturn(Collections.emptyList());

        List<ItemInfoDto> result = itemService.getByOwnerId(owner.getId(), pageable);

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());

    }

    @Test
    public void getByOwnerId_UnknownOwnerId() {

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.getByOwnerId(100L, pageable));

        assertEquals("Пользователь с id=100 не найден", throwable.getMessage(),
                "Неверный идентификатор пользователя");
    }

    @Test
    public void delete_UnknownItemId() {

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.delete(100L));

        assertEquals("Вещь с id=100 не найдена", throwable.getMessage(),
                "Неверный идентификатор вещи");
    }

    @Test
    public void getById_UnknownItemId() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.getById(1L,100L));

        assertEquals("Вещь с id=100 не найдена", throwable.getMessage(),
                "Неверный идентификатор вещи");
    }

}