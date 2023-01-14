package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongAccessException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingDate;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto create(ItemDto itemDto, long idOwner) {
        User user = getUserOrThrow(idOwner);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));

    }

    public ItemDto update(long itemId, ItemDto itemDto, long idOwner) {
        Item item = getItemOrThrow(itemId);
        if (item.getOwner().getId() != idOwner) {
            throw new WrongAccessException("To update, you need to be owner of the item");

        }
        ItemMapper.updateFromItemDto(itemDto, item);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDtoWithBookingDate get(long id) {
        return addDateToItem(getItemOrThrow(id),id);
    }

    public List<ItemDtoWithBookingDate> getByOwner(long idOwner) {
        return itemRepository.findByOwnerId(idOwner).stream()
                .map(item -> addDateToItem(item,idOwner)).collect(toList());
    }

    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text.toLowerCase()).stream().map(ItemMapper::toItemDto).collect(toList());
    }

    @Override
    public CommentDtoResponse createComment(CommentDtoRequest commentDto,long userId,long itemId) {
        User user = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);
        if (bookingRepository.findFirstByItemAndBookerAndEndBefore(item, user, now()).isEmpty()) {
            throw new ValidationException("wrong access");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, user, item));
        return CommentMapper.fromComment(comment);
    }

    private Item getItemOrThrow(long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("item with id=" + id));
    }

    private User getUserOrThrow(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("user with id = " + id));
    }

    private ItemDtoWithBookingDate addDateToItem(Item item, long userId){
        List<Booking> foo = bookingRepository.findAllByItemId(item.getId());
        LocalDateTime now = LocalDateTime.now();
        Booking last = bookingRepository.findFirstByItemIdAndStartIsBeforeOrderByStartDesc(item.getId(),now).orElse(null);
        Booking next = bookingRepository.findFirstByItemIdAndStartIsAfterOrderByStart(item.getId(), now).orElse(null);
        List<CommentDtoResponse> commentList = commentRepository.findAllByItemId(item.getId()).stream()
                .map(CommentMapper::fromComment).collect(Collectors.toList());
       return ItemMapper.toItemDtoWithBookingDate(
               item, BookingMapper.toBookingDto(last),BookingMapper.toBookingDto(next),commentList);
    }
}