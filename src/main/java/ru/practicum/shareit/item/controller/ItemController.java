package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.item.dto.ItemDto.AdvancedConstraint;
import static ru.practicum.shareit.item.dto.ItemDto.BasicConstraint;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestBody @Validated(AdvancedConstraint.class) ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") long idOwner) {
        log.info("Creating item with ownerId={}", idOwner);
        return itemService.create(itemDto, idOwner);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable("itemId") long itemId,
                          @Validated(BasicConstraint.class) @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") long idOwner) {
        log.info("Updating item id={}", itemId);
        return itemService.update(itemId, itemDto, idOwner);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable("itemId") long id) {
        log.info("Getting item id={}", id);
        return itemService.get(id);
    }

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader("X-Sharer-User-Id") long idOwner) {
        log.info("Getting items with ownerId={}", idOwner);
        return itemService.getByOwner(idOwner);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Searching matches by name or description in item with text '{}'", text);
        return itemService.search(text);
    }
}
