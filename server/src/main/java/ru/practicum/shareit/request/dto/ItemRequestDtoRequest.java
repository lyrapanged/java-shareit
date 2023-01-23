package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ItemRequestDtoRequest {

    long id;
    String description;
    LocalDateTime created;
    Long requesterId;
}
