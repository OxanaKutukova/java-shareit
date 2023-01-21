package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
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

    @NotBlank
    private String text;

    private String authorName;

    @FutureOrPresent
    private LocalDateTime created;
}
