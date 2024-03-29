package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

    @NotBlank
    @NotNull
    private String name;

    @NotNull
    @Email
    @NotBlank
    private String email;
}
