package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {

    ItemDto create(Item item, int idOwner);

    ItemDto update(int itemId, ItemDto itemDto, int idOwner);

    Optional<Item> get(int id);

    List<Item> getOwnItems(int idOwner);

    List<ItemDto> search(String text);
}
