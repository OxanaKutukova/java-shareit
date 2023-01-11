package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")

public class BookingDto {
    long id;

    @NotNull(message = "Время начала бронирования не может быть пустым")
    LocalDateTime start;

    @NotNull(message = "Время окончания бронирования не может быть пустым")
    LocalDateTime end;

    @NotNull
    Item item;

    @NotNull
    User booker;

    BookingState status;
}
