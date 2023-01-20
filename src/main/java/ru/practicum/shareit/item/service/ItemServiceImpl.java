package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingDtoShort;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.util.Constants.SORT_BY_ID_ASC;
import static ru.practicum.shareit.util.Constants.SORT_BY_START_ASC;
import static ru.practicum.shareit.util.Constants.SORT_BY_START_DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, long idOwner) {
        User user = getUserOrThrow(idOwner);
        Long requestId = itemDto.getRequestId();
        ItemRequest itemRequest = null;
        if (requestId != null) {
            itemRequest = getItemRequestOrThrow(requestId);
        }
        Item item = ItemMapper.fromItemDto(itemDto, itemRequest);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));

    }

    @Override
    @Transactional
    public ItemDto update(long itemId, ItemDto itemDto, long idOwner) {
        Item item = getItemOrThrow(itemId);
        if (item.getOwner().getId() != idOwner) {
            throw new WrongAccessException("To update, you need to be owner of the item");
        }
        ItemMapper.updateFromItemDto(itemDto, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDtoWithBookingDate get(long id, long idOwner) {
        boolean isOwner = getItemOrThrow(id).getOwner().getId() == idOwner;
        return addDateToItem(getItemOrThrow(id), isOwner);
    }

    @Override
    public List<ItemDtoWithBookingDate> getByOwner(long idOwner, Pageable pageable) {
        List<Item> items = itemRepository.findByOwnerId(idOwner, SORT_BY_ID_ASC);
        List<Comment> comments = commentRepository.findAllByItemIn(items);
        LocalDateTime now = now();
        Map<Item, List<Comment>> itemsWithComments = comments.stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.toList()));
        List<Booking> last = bookingRepository
                .findAllByItemInAndStartLessThanEqualAndStatusIs(items, now, APPROVED, SORT_BY_START_DESC);
        List<Booking> next = bookingRepository
                .findAllByItemInAndStartAfterAndStatusIs(items, now, APPROVED, SORT_BY_START_ASC);
        Map<Item, List<Booking>> lastByItem = last.stream().collect(groupingBy(Booking::getItem, toList()));
        Map<Item, List<Booking>> nextByItem = next.stream().collect(groupingBy(Booking::getItem, toList()));
        return items.stream()
                .map(item -> ItemMapper.toItemDtoWithBookingDate(
                        item,
                        lastByItem.getOrDefault(item, null) != null ?
                                lastByItem.get(item).stream().findFirst().isPresent() ?
                                        BookingMapper.toBookingDtoShort(
                                                lastByItem.get(item).stream().findFirst().get()) : null : null,
                        nextByItem.getOrDefault(item, null) != null ?
                                nextByItem.get(item).stream().findFirst().isPresent() ?
                                        BookingMapper.toBookingDtoShort(
                                                nextByItem.get(item).stream().findFirst().get()) : null : null,
                        itemsWithComments.getOrDefault(item, Collections.emptyList())
                                .stream().map(CommentMapper::fromComment).collect(toList()))).collect(toList());
    }

    @Override
    public List<ItemDto> search(String text, Pageable pageable) {
        return itemRepository.search(text.toLowerCase(), pageable).stream().map(ItemMapper::toItemDto).collect(toList());
    }

    @Override
    @Transactional
    public CommentDtoResponse createComment(CommentDtoRequest commentDto, long userId, long itemId) {
        User user = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);
        if (bookingRepository.findFirstByItemAndBookerAndEndBefore(item, user, now()).isEmpty()) {
            throw new ValidationException("wrong access");
        }
        Comment comment = CommentMapper.toComment(commentDto, user, item);
        commentRepository.save(comment);
        return CommentMapper.fromComment(comment);
    }

    private Item getItemOrThrow(long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("item with id=" + id));
    }

    private User getUserOrThrow(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("user with id = " + id));
    }

    private ItemDtoWithBookingDate addDateToItem(Item item, boolean isOwner) {
        LocalDateTime now = LocalDateTime.now();
        Booking last = null;
        Booking next = null;
        if (isOwner) {
            last = bookingRepository.findFirstByItemAndStartLessThanEqualAndStatusIs(
                    item, now, APPROVED, SORT_BY_START_DESC).orElse(null);
            next = bookingRepository.findFirstByItemAndStartAfterAndStatusIs(
                    item, now, APPROVED, SORT_BY_START_ASC).orElse(null);
        }
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<CommentDtoResponse> commentsDto = comments.stream()
                .map(CommentMapper::fromComment).collect(Collectors.toList());
        return ItemMapper.toItemDtoWithBookingDate(
                item, last != null ? toBookingDtoShort(last) : null,
                next != null ? toBookingDtoShort(next) : null, commentsDto);
    }

    private ItemRequest getItemRequestOrThrow(long id) {
        return itemRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("requestId = " + id));
    }
}
