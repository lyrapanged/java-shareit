package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Value
@Builder
public class CommentDtoRequest {

    @NotBlank(groups = BasicConstraint.class)
    String text;
    Long itemId;
    Long authorId;
    LocalDateTime created;

    public interface BasicConstraint {
    }
}
