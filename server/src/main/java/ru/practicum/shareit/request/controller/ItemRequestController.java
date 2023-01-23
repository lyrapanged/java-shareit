package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.util.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoResponse create(@RequestBody ItemRequestDtoRequest itemRequestDtoRequest,
                                         @RequestHeader(X_SHARER_USER_ID) long ownerId) {
        log.info("Creating request by  userId={}", ownerId);
        return itemRequestService.create(itemRequestDtoRequest, ownerId);
    }

    @GetMapping
    public List<ItemRequestDtoResponseWithItems> getByOwner(@RequestHeader(X_SHARER_USER_ID) long ownerId) {
        log.info("Getting request by  userId={}", ownerId);
        return itemRequestService.getByOwner(ownerId);

    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponseWithItems> getAll(@RequestHeader(X_SHARER_USER_ID) long ownerId,
                                                        @RequestParam(name = "from", defaultValue = "0")
                                                        Integer from,
                                                        @RequestParam(name = "size", defaultValue = "10")
                                                        Integer size) {
        log.info("Getting ALL request by  userId={}", ownerId);
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return itemRequestService.getAll(ownerId, PageRequest.of(page, size, sort));
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoResponseWithItems get(@RequestHeader(X_SHARER_USER_ID) long ownerId,
                                               @PathVariable long requestId) {
        return itemRequestService.get(ownerId, requestId);
    }
}
