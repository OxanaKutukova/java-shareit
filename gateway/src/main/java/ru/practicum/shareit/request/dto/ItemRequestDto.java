package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")
public class ItemRequestDto {
    private Long id;

    @NotBlank
    private String description;

    @FutureOrPresent
    private LocalDateTime created;
}