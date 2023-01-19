package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        return ItemRequestDtoResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequestDtoResponseWithItems toItemRequestDtoResponseWithItems(ItemRequest itemRequest,
                                                                             List<ItemDto> items) {
        return ItemRequestDtoResponseWithItems.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public ItemRequest toItemRequest(ItemRequestDtoRequest itemRequestDtoRequest, User requester) {
        return ItemRequest.builder()
                .id(itemRequestDtoRequest.getId())
                .description(itemRequestDtoRequest.getDescription())
                .requester(requester)
                .created(itemRequestDtoRequest.getCreated())
                .build();
    }
}
