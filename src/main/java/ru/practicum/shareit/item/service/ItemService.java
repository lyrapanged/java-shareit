package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Autowired
    public ItemService(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    public ItemDto create(ItemDto itemDto, int idOwner) {
        User user = userDao.get(idOwner).orElseThrow(() -> new NotFoundException("User with id = " + idOwner));
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setId(idOwner);
        item.setOwner(user);
        return itemDao.create(item, idOwner);

    }

    public ItemDto update(int itemId, ItemDto itemDto, int idOwner) {
        if (getNotOptional(itemId).getOwner().getId() != idOwner) {
            throw new WrongAccessException("To update, you need to be owner of the item");
        }
        return itemDao.update(itemId, itemDto, idOwner);
    }

    public ItemDto get(int id) {
        return ItemMapper.toItemDto(getNotOptional(id));
    }

    public List<ItemDto> getOwnItems(int idOwner) {
        return itemDao.getOwnItems(idOwner).stream().map(ItemMapper::toItemDto).collect(toList());
    }

    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemDao.search(text);
    }

    private Item getNotOptional(int id) {
        return itemDao.get(id).orElseThrow(() -> new NotFoundException("item with id"));
    }
}
