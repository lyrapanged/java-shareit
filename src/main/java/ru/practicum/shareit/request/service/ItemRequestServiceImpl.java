package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDtoResponse create(ItemRequestDtoRequest itemRequestDtoRequest, long ownerId) {
        User requester = getUserOrThrow(ownerId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoRequest, requester);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDtoResponse(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoResponseWithItems> getByOwner(long ownerId) {
        User requester = getUserOrThrow(ownerId);
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorId(requester.getId(), Sort.by("created"));
        List<Item> items = itemRepository.findAllByItemRequestIn(requests);
        return getItemRequestDtoResponseWithItems(requests, items);
    }


    @Override
    public List<ItemRequestDtoResponseWithItems> getAll(long ownerId, Pageable pageable) {
        User owner = getUserOrThrow(ownerId);
        List<ItemRequest> allByRequestorIsNot = itemRequestRepository.findAllByRequestorIsNot(owner, pageable);
        List<Item> allByItemRequestIn = itemRepository.findAllByItemRequestIn(allByRequestorIsNot);
        return getItemRequestDtoResponseWithItems(allByRequestorIsNot, allByItemRequestIn);
    }

    @Override
    public ItemRequestDtoResponseWithItems get(long ownerId, long requestId) {
        getUserOrThrow(ownerId);
        ItemRequest itemRequest = getItemRequestOrThrow(requestId);
        List<Item> allByItemRequest = itemRepository.findAllByItemRequest(itemRequest);
        List<ItemDto> itemsDto = new ArrayList<>();
        if (!allByItemRequest.isEmpty()) {
            itemsDto = allByItemRequest.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        }
        return ItemRequestMapper.toItemRequestDtoResponseWithItems(itemRequest, itemsDto);
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("user with id=" + userId));
    }

    private ItemRequest getItemRequestOrThrow(long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("user with id=" + requestId));
    }

    private static List<ItemRequestDtoResponseWithItems> getItemRequestDtoResponseWithItems(List<ItemRequest> requests,
                                                                                            List<Item> items) {
        List<ItemDto> itemDto = new ArrayList<>();
        if (!items.isEmpty()) {
            itemDto = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        }
        Map<Long, List<ItemDto>> itemsWithIdRequest;
        if (!itemDto.isEmpty()) {
            itemsWithIdRequest = itemDto.stream()
                    .collect(Collectors.groupingBy(ItemDto::getRequestId));
        } else {
            itemsWithIdRequest = new HashMap<>();
        }
        if (!requests.isEmpty()) {
            return requests.stream()
                    .map(itemRequest -> ItemRequestMapper.toItemRequestDtoResponseWithItems(itemRequest,
                            itemsWithIdRequest.getOrDefault(itemRequest.getRequestor().getId(), Collections.emptyList())))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
