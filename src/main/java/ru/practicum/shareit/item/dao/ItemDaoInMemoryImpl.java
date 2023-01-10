package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
public class ItemDaoInMemoryImpl implements ItemDao {

    private final Map<Integer, List<Item>> items = new HashMap<>();
    private int id = 0;

    @Override
    public ItemDto create(Item item, int idOwner) {
        if (items.get(idOwner) != null) {
            item.setId(++id);
            items.get(idOwner).add(item);
        } else {
            List<Item> things = new ArrayList<>();
            item.setId(++id);
            things.add(item);
            items.put(idOwner, things);
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(int itemId, ItemDto itemDto, int idOwner) {
        Item item = items.get(idOwner).stream()
                .findAny().orElseThrow(() -> new NotFoundException("item with id =" + itemId));
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription((itemDto.getDescription()));
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Optional<Item> get(int id) {
        return items.values().stream()
                .flatMap(itemList -> itemList.stream()
                        .filter(item -> item.getId() == id))
                .findAny();
    }

    @Override
    public List<Item> getOwnItems(int idOwner) {
        return items.get(idOwner);
    }

    @Override
    public List<ItemDto> search(String text) {
        return items.values().stream().flatMap(itemList -> itemList.stream()
                        .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase()))
                                && item.getAvailable()))
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }
}
