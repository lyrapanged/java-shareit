package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
public class ItemDaoInMemoryImpl implements ItemDao {

    private final Map<Long, Map<Long, Item>> items = new HashMap<>();
    private long id = 0;

    @Override
    public Item create(Item item, long idOwner) {
        Map<Long, Item> itemsTemporary = items.computeIfAbsent(idOwner, k -> new HashMap<>());
        item.setId(++id);
        itemsTemporary.put(item.getId(), item);
        items.put(idOwner, itemsTemporary);
        return item;
    }

    @Override
    public Item update(long itemId, ItemDto itemDto, long idOwner) {
        Item item = this.get(itemId).orElseThrow(() -> new NotFoundException("item with id =" + itemId));
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription((itemDto.getDescription()));
        }
        return item;
    }

    @Override
    public Optional<Item> get(long id) {
        return items.values().stream().filter(x -> x.get(id) != null).findAny().map(x -> x.get(id));
    }

    @Override
    public List<Item> getByOwner(long idOwner) {
        return new ArrayList<>(items.get(idOwner).values());
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream().flatMap(itemList -> itemList.values().stream()
                        .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase()))
                                && item.getAvailable()))
                .collect(toList());
    }
}
