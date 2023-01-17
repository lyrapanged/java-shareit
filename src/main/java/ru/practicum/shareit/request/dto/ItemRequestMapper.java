package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        return  ItemRequestDtoResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDtoResponseWithItems toItemRequestDtoResponseWithItems(ItemRequest itemRequest,
                                                                                    List<ItemDto> items) {
        return ItemRequestDtoResponseWithItems.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDtoRequest itemRequestDtoRequest, User requestor){
        return ItemRequest.builder()
                .id(itemRequestDtoRequest.getId())
                .description(itemRequestDtoRequest.getDescription())
                .requestor(requestor)
                .created(itemRequestDtoRequest.getCreated())
                .build();
    }
}
