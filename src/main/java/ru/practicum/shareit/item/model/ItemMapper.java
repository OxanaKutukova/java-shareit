package ru.practicum.shareit.item.model;


import ru.practicum.shareit.booking.dto.BookingForItemInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import java.util.ArrayList;


public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        final ItemDto itemDto = ItemDto
                                      .builder()
                                       .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .build();

        ItemRequest itemRequest = item.getItemRequest();
        if (itemRequest != null) {
            itemDto.setRequestId(itemRequest.getId());
        }

        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return Item
                .builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
    }

    public static ItemInfoDto toItemInfoDto(Item item, BookingForItemInfoDto lastBooking,
                                            BookingForItemInfoDto nextBooking) {
        return ItemInfoDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(new ArrayList<>())
                .build();
    }
}
