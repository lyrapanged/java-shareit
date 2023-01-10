package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestBody @Validated(AdvancedConstraint.class) ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") int idOwner) {
        log.info("Create item with ownerId={}", idOwner);
        return itemService.create(itemDto, idOwner);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable("itemId") int itemId,
                          @Validated(BasicConstraint.class) @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") int idOwner) {
        log.info("Update item id={}", itemId);
        return itemService.update(itemId, itemDto, idOwner);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable("itemId") int id) {
        log.info("Get item id={}", id);
        return itemService.get(id);
    }

    @GetMapping
    public List<ItemDto> getOwnItems(@RequestHeader("X-Sharer-User-Id") int idOwner) {
        log.info("Get items with ownerId={}", idOwner);
        return itemService.getOwnItems(idOwner);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Search matches by name or description in item with text '{}'", text);
        return itemService.search(text);
    }
}
