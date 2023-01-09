package ru.practicum.shareit.booking.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")
public class BookingForItemInfoDto {
    private Long id;
    private Long bookerId;
}
