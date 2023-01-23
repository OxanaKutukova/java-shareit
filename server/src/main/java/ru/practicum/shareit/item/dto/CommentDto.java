package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")

public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
