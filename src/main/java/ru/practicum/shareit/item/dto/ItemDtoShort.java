package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemDtoShort {

    Long idItem;
    String name;
    Long idOwner;
    Long idRequest;
}
