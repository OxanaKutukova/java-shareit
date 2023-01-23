package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
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
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemInfoDto> getByOwnerId(Long userId, Pageable pageable) {
        throwIfNotExistUser(userId);
        final List<Item> items = itemRepository.findByOwnerId(userId, pageable);
        final List<ItemInfoDto> res = new ArrayList<>();
        for (Item item: items) {
            BookingForItemInfoDto lastBookingInfoDto = null;
            BookingForItemInfoDto nextBookingInfoDto = null;
            final Booking lastBooking = bookingRepository
                    .findFirstByItem_Owner_IdAndItemIdAndEndIsBefore(userId,
                                                                     item.getId(),
                                                                     LocalDateTime.now(),
                                                                     Sort.by(Sort.Direction.DESC, "end"));
            final Booking nextBooking = bookingRepository
                    .findFirstByItem_Owner_IdAndItemIdAndStartIsAfter(userId,
                                                                      item.getId(),
                                                                      LocalDateTime.now(),
                                                                      Sort.by(Sort.Direction.DESC, "start"));
            if (lastBooking != null) {
                lastBookingInfoDto = BookingMapper.toBookingForItemInfo(lastBooking);
            }
            if (nextBooking != null) {
                nextBookingInfoDto = BookingMapper.toBookingForItemInfo(nextBooking);
            }
            ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item, lastBookingInfoDto, nextBookingInfoDto);
            setCommentsToItemInfoDto(itemInfoDto);
            res.add(itemInfoDto);
        }

        return res;
    }

    @Override
    public ItemInfoDto getById(Long userId, Long itemId) {
        throwIfNotExistUser(userId);
        final Item item = getItemById(itemId);
        BookingForItemInfoDto lastBookingInfoDto = null;
        BookingForItemInfoDto nextBookingInfoDto = null;
        final Booking lastBooking = bookingRepository
                .findFirstByItem_Owner_IdAndItemIdAndEndIsBefore(userId,
                                                                 itemId,
                                                                 LocalDateTime.now(),
                                                                 Sort.by(Sort.Direction.DESC, "end"));
        final Booking nextBooking = bookingRepository
                .findFirstByItem_Owner_IdAndItemIdAndStartIsAfter(userId,
                                                                  itemId,
                                                                  LocalDateTime.now(),
                                                                  Sort.by(Sort.Direction.DESC, "start"));
        if (lastBooking != null) {
            lastBookingInfoDto = BookingMapper.toBookingForItemInfo(lastBooking);
        }
        if (nextBooking != null) {
            nextBookingInfoDto = BookingMapper.toBookingForItemInfo(nextBooking);
        }
        ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item, lastBookingInfoDto, nextBookingInfoDto);

        setCommentsToItemInfoDto(itemInfoDto);

        return itemInfoDto;
    }

    @Transactional
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {

        //Проверим пользователя
        final User user = getUserById(userId);
        final Item item = ItemMapper.toItem(itemDto, user);
        final Long requestId = itemDto.getRequestId();

        if (requestId != null) {
            item.setItemRequest(itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Неверный идентификатор запроса")));
        }
        final Item itemS = itemRepository.save(item);

        return ItemMapper.toItemDto(itemS);
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {

        //Проверим пользователя
        final User user = getUserById(userId);
        //Проверим вещь
        final Item itemU = getItemById(itemId);
        //Проверим владельца вещи
        if (!itemU.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " может изменять только владелец");
        }
        final Item item = ItemMapper.toItem(itemDto, user);
        if (item.getName() != null) {
            itemU.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemU.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemU.setAvailable(item.getAvailable());
        }
        itemU.setOwner(user);
        final Item itemSaved = itemRepository.save(itemU);

        return ItemMapper.toItemDto(itemSaved);
    }

    @Transactional
    @Override
    public void delete(Long itemId) {
        throwIfNotExistItem(itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> getByNameByDirector(String text, Pageable pageable) {
        if (!text.isBlank()) {
            return itemRepository.search(text, pageable)
                    .stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Transactional
    @Override
    public CommentDto createComment(Long userId, CommentDto commentDto, Long itemId) {
        //Проверим пользователя
        final User user = getUserById(userId);
        //Проверим вещь
        final Item item = getItemById(itemId);

        //Проверим что пользователь брал вещь в аренду
        List<Booking> bookings = bookingRepository
                .findAllByBooker_Id_AndItem_Id_AndEndIsBefore(userId,
                                                              itemId,
                                                              LocalDateTime.now(),
                                                              Sort.by(Sort.Direction.DESC, "start"));
        if (bookings.isEmpty()) {
            throw new BadRequestException("Только по бронированной вещи можно добавить комментарий");
        } else {
            final Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, user, item));

            return CommentMapper.toCommentDto(comment);
        }
    }

    private void setCommentsToItemInfoDto(ItemInfoDto itemInfoDto) {

        List<Comment> comments = commentRepository.findAllByItem_Id(itemInfoDto.getId(),
                Sort.by(Sort.Direction.ASC, "created"));

        if (!comments.isEmpty()) {
            itemInfoDto.setComments(comments
                    .stream().map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList())
            );
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private void throwIfNotExistUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
    }

    private void throwIfNotExistItem(Long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
    }
}
