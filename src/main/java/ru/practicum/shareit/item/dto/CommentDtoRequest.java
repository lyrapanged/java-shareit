package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Value
@Builder
public class CommentDtoRequest {
    Long id;
    @NotBlank
    String text;
    Long itemId;
    Long authorId;
    LocalDateTime created;
}
