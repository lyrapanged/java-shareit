package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {

    ItemService itemService;
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setup() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                itemRequestRepository);
    }

    @Test
    void createIsOk() {
        User user = new User(1L, "name1", "email1@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item_name")
                .build();
        when(itemRepository.save(any())).thenReturn(ItemMapper.fromItemDto(itemDto, null));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        ItemDto itemDtoRequest = itemService.create(itemDto, user.getId());
        assertEquals(itemDto.getName(), itemDtoRequest.getName());
        assertEquals(itemDto.getAvailable(), itemDtoRequest.getAvailable());
        assertEquals(itemDto.getDescription(), itemDtoRequest.getDescription());
    }

    @Test
    void create_UserDoestExist_ThenTrow() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item_name")
                .build();
        when(itemRepository.save(any())).thenReturn(ItemMapper.fromItemDto(itemDto, null));
        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.create(itemDto, 20L));
        assertEquals("This user with id = 20 does not exist", e.getMessage());
    }

    @Test
    void create_BadIdRequest_ThenTrow() {
        User user = new User(1L, "name1", "email1@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item_name")
                .requestId(20L)
                .build();
        when(itemRepository.save(any())).thenReturn(ItemMapper.fromItemDto(itemDto, null));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.create(itemDto, 1));
        assertEquals("This requestId = 20 does not exist", e.getMessage());
    }

    @Test
    void update() {
        User user = new User(1L, "name1", "email1@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("update")
                .build();
        Item itemWithUpdateName = Item.builder()
                .id(1L)
                .name("updatedName")
                .description("updatedDescription")
                .owner(user)
                .build();
        when(itemRepository.save(any())).thenReturn(ItemMapper.fromItemDto(itemDto, null));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(itemWithUpdateName));
        ItemDto itemDtoRequest = itemService.update(itemDto.getId(), itemDto, user.getId());
        assertEquals(itemWithUpdateName.getName(), itemDtoRequest.getName());
        assertEquals(itemWithUpdateName.getId(), itemDtoRequest.getId());
        assertEquals(itemWithUpdateName.getAvailable(), itemDtoRequest.getAvailable());
        assertEquals(itemWithUpdateName.getDescription(), itemDtoRequest.getDescription());
    }

    @Test
    void updateWithWrongItemOwnerId() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user20 = new User(20L, "name20", "email10@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("update")
                .build();
        Item itemWithUpdateName = Item.builder()
                .id(1L)
                .name("updatedName")
                .description("updatedDescription")
                .owner(user)
                .build();
        when(itemRepository.save(any())).thenReturn(ItemMapper.fromItemDto(itemDto, null));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(itemWithUpdateName));
        ItemDto itemDtoRequest = itemService.update(itemDto.getId(), itemDto, user.getId());
        WrongAccessException e = assertThrows(WrongAccessException.class, () -> itemService
                .update(itemDto.getId(), itemDtoRequest, user20.getId()));
        assertEquals("To update, you need to be owner of the item", e.getMessage());
    }

    @Test
    void get() {
        User user = new User(1L, "name1", "email1@gmail.com");
        Item item = new Item(1L, "item_name", "item_name", true, user, null);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(new ArrayList<>());
        ItemDtoWithBookingDate itemDtoWithBookingDate = itemService.get(item.getId(), user.getId());
        assertEquals(item.getId(), itemDtoWithBookingDate.getId());
        assertEquals(item.getName(), itemDtoWithBookingDate.getName());
        assertEquals(item.getAvailable(), itemDtoWithBookingDate.getAvailable());
        assertEquals(item.getDescription(), itemDtoWithBookingDate.getDescription());
    }

    @Test
    void getByOwner() {
        User user = new User(1L, "name1", "email1@gmail.com");
        Item itemWithUpdateName = Item.builder()
                .id(1L)
                .name("dName")
                .description("Description")
                .owner(user)
                .build();
        when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(Collections.singletonList(itemWithUpdateName));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(itemWithUpdateName));
        List<ItemDtoWithBookingDate> itemList = itemService.getByOwner(user.getId(), Pageable.ofSize(10));
        assertEquals(1, itemList.size());
        assertEquals(itemWithUpdateName.getId(), itemList.get(0).getId());
        assertEquals(itemWithUpdateName.getName(), itemList.get(0).getName());
        assertEquals(itemWithUpdateName.getAvailable(), itemList.get(0).getAvailable());
        assertEquals(itemWithUpdateName.getDescription(), itemList.get(0).getDescription());
    }

    @Test
    void getByOwner_Check_LastAndNextBooking() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email1@gmail.com");
        User user3 = new User(3L, "name3", "email1@gmail.com");
        User user4 = new User(4L, "name4", "email1@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusMinutes(12),
                LocalDateTime.now().minusMinutes(13), item, user2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusMinutes(14),
                LocalDateTime.now().plusMinutes(14), item, user3, BookingStatus.APPROVED);
        Booking booking3 = new Booking(3L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, user4, BookingStatus.APPROVED);
        when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(Collections.singletonList(item));
        when(bookingRepository.findAllItemOwnerBookings(anyLong(), any())).thenReturn(List.of(booking1, booking2, booking3));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItemInAndStartLessThanEqualAndStatusIs(any(), any(), any(), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findAllByItemInAndStartAfterAndStatusIs(any(), any(), any(), any()))
                .thenReturn(List.of(booking3));
        List<ItemDtoWithBookingDate> itemList = itemService.getByOwner(user.getId(), Pageable.ofSize(10));
        assertEquals(1, itemList.size());
        assertEquals(item.getId(), itemList.get(0).getId());
        assertEquals(item.getName(), itemList.get(0).getName());
        assertEquals(item.getAvailable(), itemList.get(0).getAvailable());
        assertEquals(item.getDescription(), itemList.get(0).getDescription());
        assertEquals(booking1.getId(), itemList.get(0).getLastBooking().getId());
        assertEquals(booking3.getId(), itemList.get(0).getNextBooking().getId());
    }

    @Test
    void search() {
        User user = new User(1L, "name1", "email1@gmail.com");
        Item item = new Item(1L, "item_name", "item_name", true, user, null);
        when(itemRepository.search(anyString(), any())).thenReturn(Collections.singletonList(item));
        List<ItemDto> itemList = itemService.search("item_name", Pageable.ofSize(10));
        assertEquals(1, itemList.size());
        assertEquals(item.getId(), itemList.get(0).getId());
        assertEquals(item.getName(), itemList.get(0).getName());
        assertEquals(item.getAvailable(), itemList.get(0).getAvailable());
        assertEquals(item.getDescription(), itemList.get(0).getDescription());
    }

    @Test
    void createComment() {
        User user = new User(1L, "name1", "email1@gmail.com");
        Item item = new Item(1L, "item_name", "item_name", true, user, null);
        User user2 = new User(2L, "name2", "email1@gmail.com");
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusMinutes(12),
                LocalDateTime.now().minusMinutes(13), item, user2, BookingStatus.APPROVED);
        CommentDtoRequest commentDto = CommentDtoRequest.builder()
                .text("some text")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemAndBookerAndEndBefore(any(), any(), any())).thenReturn(Optional.of(booking1));
        when(commentRepository.save(any())).thenReturn(CommentMapper.toComment(commentDto, user2, item));
        CommentDtoResponse newComment = itemService.createComment(commentDto, user2.getId(), item.getId());
        assertEquals(newComment.getText(), commentDto.getText());
    }

    @Test
    void createCommentWithWrongAccess() {
        User user = new User(1L, "name1", "email1@gmail.com");
        Item item = new Item(1L, "item_name", "item_name", true, user, null);
        User user2 = new User(2L, "name2", "email1@gmail.com");
        CommentDtoRequest commentDto = CommentDtoRequest.builder()
                .text("some text")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemAndBookerAndEndBefore(any(), any(), any())).thenReturn(Optional.empty());
        when(commentRepository.save(any())).thenReturn(CommentMapper.toComment(commentDto, user2, item));
        ValidationException e = assertThrows(ValidationException.class,
                () -> itemService.createComment(commentDto, user2.getId(), item.getId()));
        assertEquals("wrong access", e.getMessage());
    }
}