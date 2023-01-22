package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static java.util.Collections.emptyList;
import static ru.practicum.shareit.item.dto.ItemDto.AdvancedConstraint;
import static ru.practicum.shareit.item.dto.ItemDto.BasicConstraint;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(AdvancedConstraint.class) ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") long idOwner) {
        log.info("Creating item with ownerId={}", idOwner);
        return itemClient.create(itemDto, idOwner);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable("itemId") long itemId, @Validated(BasicConstraint.class)
    @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long idOwner) {
        log.info("Updating item id={}", itemId);
        return itemClient.update(itemId, itemDto, idOwner);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable("itemId") long id, @RequestHeader("X-Sharer-User-Id") long idOwner) {
        log.info("Getting item id={}", id);
        return itemClient.get(id, idOwner);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") long idOwner,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                             Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10")
                                             Integer size) {
        log.info("Getting items with ownerId={}", idOwner);
        return itemClient.getByOwner(idOwner, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Searching matches by name or description in item with text '{}'", text);
        return text.isBlank() ? ResponseEntity.ok(emptyList()) : itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDtoRequest comment,
                                                @RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemClient.createComment(comment, userId, itemId);
    }
}
