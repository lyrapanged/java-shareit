package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseWithItems;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoResponse create(ItemRequestDtoRequest itemRequestDtoRequest, long ownerId);

    List<ItemRequestDtoResponseWithItems> getByOwner(long ownerId);

    List<ItemRequestDtoResponseWithItems> getAll(long ownerId, Pageable pageRequest);

    ItemRequestDtoResponseWithItems get(long ownerId, long requestId);
}
