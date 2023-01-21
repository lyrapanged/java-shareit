package java.ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.ru.practicum.shareit.item.dto.CommentDtoRequest;
import java.ru.practicum.shareit.item.dto.ItemDto;
import java.util.Collections;

import static java.ru.practicum.shareit.util.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(ItemDto.AdvancedConstraint.class) ItemDto itemDto,
                                         @RequestHeader(X_SHARER_USER_ID) long idOwner) {
        log.info("Creating item with ownerId={}", idOwner);
        return itemClient.create(itemDto, idOwner);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable("itemId") long itemId, @Validated(ItemDto.BasicConstraint.class)
    @RequestBody ItemDto itemDto, @RequestHeader(X_SHARER_USER_ID) long idOwner) {
        log.info("Updating item id={}", itemId);
        return itemClient.update(itemDto, itemId, idOwner);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable("itemId") long id, @RequestHeader(X_SHARER_USER_ID) long idOwner) {
        log.info("Getting item id={}", id);
        return itemClient.get(id, idOwner);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwner(@RequestHeader(X_SHARER_USER_ID) long idOwner,
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
        return text.isBlank() ? ResponseEntity.ok(Collections.emptyList()) : itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDtoRequest comment,
                                                @RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        return itemClient.createComment(comment, userId, itemId);
    }
}