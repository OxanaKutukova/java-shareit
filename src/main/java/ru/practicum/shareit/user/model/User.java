package ru.practicum.shareit.user.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")

public class User {
    private Long id;
    private String name;
    private String email;
}