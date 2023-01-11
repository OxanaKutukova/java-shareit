package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")
public class BookingInDto {
    private Long id;

    @NotNull(message = "Время начала бронирования не может быть пустым")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования не может быть пустым")
    @FutureOrPresent
    private LocalDateTime end;

    private Long itemId;
}
