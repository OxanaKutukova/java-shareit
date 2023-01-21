package ru.practicum.shareit.user.dto;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")

public class UserDto {
    private Long id;
    private String name;
    private String email;
}
