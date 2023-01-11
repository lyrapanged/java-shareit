package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {

    Item create(Item item, long idOwner);

    Item update(long itemId, ItemDto itemDto, long idOwner);

    Optional<Item> get(long id);

    List<Item> getByOwner(long idOwner);

    List<Item> search(String text);
}
