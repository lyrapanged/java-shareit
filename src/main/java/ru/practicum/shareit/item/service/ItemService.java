package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongAccessException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    public ItemDto create(ItemDto itemDto, long idOwner) {
        User user = userDao.get(idOwner).orElseThrow(() -> new NotFoundException("User with id = " + idOwner));
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemDao.create(item, idOwner));

    }

    public ItemDto update(long itemId, ItemDto itemDto, long idOwner) {
        if (getOrThrow(itemId).getOwner().getId() != idOwner) {
            throw new WrongAccessException("To update, you need to be owner of the item");

        }
        return ItemMapper.toItemDto(itemDao.update(itemId, itemDto, idOwner));
    }

    public ItemDto get(long id) {
        return ItemMapper.toItemDto(getOrThrow(id));
    }

    public List<ItemDto> getByOwner(long idOwner) {
        return itemDao.getByOwner(idOwner).stream().map(ItemMapper::toItemDto).collect(toList());
    }

    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemDao.search(text).stream().map(ItemMapper::toItemDto).collect(toList());
    }

    private Item getOrThrow(long id) {
        return itemDao.get(id).orElseThrow(() -> new NotFoundException("item with id"));
    }
}
