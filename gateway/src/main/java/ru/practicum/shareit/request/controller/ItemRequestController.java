package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDtoRequest itemRequestDtoRequest,
                                         @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Creating request by  userId={}", ownerId);
        return itemRequestClient.create(itemRequestDtoRequest, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Getting request by  userId={}", ownerId);
        return itemRequestClient.getByOwner(ownerId);

    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                         Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10")
                                         Integer size) {
        log.info("Getting ALL request by  userId={}", ownerId);
        return itemRequestClient.getAll(from, size, ownerId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                      @PathVariable long requestId) {
        return itemRequestClient.get(ownerId, requestId);
    }
}
