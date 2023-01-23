package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceTest {
    private ItemRequestService itemRequestService;
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @BeforeEach
    void beforeEach() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

    }

    @Test
    void create() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final ItemRequest itemRequest = new ItemRequest(1L, "ItemRequestDescription", user, dateTime);
        final ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.save(itemRequest))
                .thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.create(user.getId(), itemRequestDto);

        Assertions.assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(itemRequestDto.getCreated(), result.getCreated());
    }

    @Test
    void getById() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final ItemRequest itemRequest = new ItemRequest(1L, "ItemRequestDescription", user, dateTime);
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        ItemRequestInfoDto result = itemRequestService.getById(user.getId(), itemRequest.getId());

        Assertions.assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
    }

    @Test
    void getAllWithPage() {
        final PageRequest pageRequest = PageRequest.of(0 / 20, 20, Sort.by(Sort.Direction.DESC, "created"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final ItemRequest itemRequest = new ItemRequest(1L, "ItemRequestDescription", user, dateTime);
        final ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(itemRequest);
        when(itemRequestRepository.findAllByNotRequestorId(user.getId(), pageRequest))
                .thenReturn(new ArrayList<ItemRequest>());
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        List<ItemRequestInfoDto> result = itemRequestService.getAllWithPage(user.getId(), pageRequest);

        Assertions.assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAll() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2023-03-01 10:15:30", formatter);
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        final ItemRequest itemRequest = new ItemRequest(1L, "ItemRequestDescription", user, dateTime);
        final ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(itemRequest);
        when(itemRequestRepository.findAllByRequestor_Id(user.getId(), Sort.by(Sort.Direction.DESC, "created")))
                .thenReturn(Collections.singletonList(itemRequest));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        List<ItemRequestInfoDto> result = itemRequestService.getAll(user.getId());

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestInfoDto, result.get(0));
    }

    @Test
    public void getByUnknownId() {
        final User user = new User(1L, "Oksi", "oksi.dto@ya.ru");
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemRequestService.getById(1L,100L));

        assertEquals("Запрос на вещь с id=100 не найден", throwable.getMessage(),
                "Неверный идентификатор запроса на вещь");
    }

    @Test
    public void getByUnknownUserId() {
        Throwable throwable = assertThrows(NotFoundException.class, () -> itemRequestService.getById(100L,100L));

        assertEquals("Пользователь с id=100 не найден", throwable.getMessage(),
                "Неверный идентификатор пользователя");
    }
}