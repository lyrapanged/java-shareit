package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Value
@Builder
public class ItemRequestDtoRequest {

    long id;
    @NotBlank String description;
    LocalDateTime created;
    Long requesterId;
}
