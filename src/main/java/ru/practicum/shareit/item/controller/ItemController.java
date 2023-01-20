package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingDate;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemDto.AdvancedConstraint;
import static ru.practicum.shareit.item.dto.ItemDto.BasicConstraint;
import static ru.practicum.shareit.util.Constants.SORT_BY_ID_ASC;
import static ru.practicum.shareit.util.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestBody @Validated(AdvancedConstraint.class) ItemDto itemDto,
                          @RequestHeader(X_SHARER_USER_ID) long idOwner) {
        log.info("Creating item with ownerId={}", idOwner);
        return itemService.create(itemDto, idOwner);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable("itemId") long itemId, @Validated(BasicConstraint.class)
    @RequestBody ItemDto itemDto, @RequestHeader(X_SHARER_USER_ID) long idOwner) {
        log.info("Updating item id={}", itemId);
        return itemService.update(itemId, itemDto, idOwner);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingDate get(@PathVariable("itemId") long id, @RequestHeader(X_SHARER_USER_ID) long idOwner) {
        log.info("Getting item id={}", id);
        return itemService.get(id, idOwner);
    }

    @GetMapping
    public List<ItemDtoWithBookingDate> getByOwner(@RequestHeader(X_SHARER_USER_ID) long idOwner,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                   Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10")
                                                   Integer size) {
        log.info("Getting items with ownerId={}", idOwner);
        int page = from / size;
        final Pageable pageable = PageRequest.of(page, size, SORT_BY_ID_ASC);
        return itemService.getByOwner(idOwner, pageable);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Searching matches by name or description in item with text '{}'", text);
        int page = from / size;
        final Pageable pageable = PageRequest.of(page, size, SORT_BY_ID_ASC);
        return text.isBlank() ? Collections.emptyList() : itemService.search(text, pageable);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(@Valid @RequestBody CommentDtoRequest comment,
                                            @RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        return itemService.createComment(comment, userId, itemId);
    }
}
