package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.service.BookingState.ALL;
import static ru.practicum.shareit.booking.service.BookingState.CURRENT;
import static ru.practicum.shareit.booking.service.BookingState.FUTURE;
import static ru.practicum.shareit.booking.service.BookingState.PAST;
import static ru.practicum.shareit.booking.service.BookingState.REJECTED;
import static ru.practicum.shareit.booking.service.BookingState.WAITING;

class BookingServiceImplTest {

    BookingService bookingService;
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;

    @BeforeEach
    void setup() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository,
                itemRepository);
    }

    @Test
    void create_IsOk() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user2, item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        BookingDtoResponse bookingDto = bookingService.create(bookingDtoRequest, user2.getId());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
    }

    @Test
    void create_ByWrongId() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user2, item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.create(bookingDtoRequest, user.getId()));
        assertEquals("This id should be equal booker id. Entity does not exist", e.getMessage());
    }

    @Test
    void create_ByWrongAvailableForBooking() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", false, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user2, item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        ValidationException e = assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoRequest, user2.getId()));
        assertEquals("Item must  be available for booking", e.getMessage());
    }


    @Test
    void updateStatus_IsOk() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        BookingDtoResponse bookingDto = bookingService.updateStatus(0, user.getId(), true);
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void updateStatus_SameStatus_throw() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        bookingService.updateStatus(0, user.getId(), true);
        ValidationException e = assertThrows(ValidationException.class, () -> bookingService.updateStatus(0, user.getId(), true));
        assertEquals("Nothing to changing", e.getMessage());
    }

    @Test
    void updateStatus_BadId_throw() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        Item item2 = new Item(2L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        BookingDtoRequest bookingDtoRequest2 = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item2.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest2, user2, item2));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.updateStatus(0, user2.getId(), true));
        assertEquals("This Access does not exist", e.getMessage());
    }

    @Test
    void get_ByWrongId_thenThrow() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.get(0,
                2L));
        assertEquals("This booking with id=0 does not exist", e.getMessage());
    }

    @Test
    void get_ByWrongBooking_thenThrow() {
        User user = new User(1L, "name1", "email1@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user, item)));
        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.get(0,
                2L));
        assertEquals("This access does not exist", e.getMessage());
    }

    @Test
    void getAllByBookerTest() {
        User user = new User(1L, "name1", "email1@gmail.com");
        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.getAllByBooker(user.getId(),
                ALL, Pageable.unpaged()));
        assertEquals("This user with id=1 does not exist", e.getMessage());
    }

    @Test
    void getAllByOwner() {
        User user = new User(1L, "name1", "email1@gmail.com");
        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.getAllByOwner(user.getId(),
                ALL, Pageable.unpaged()));
        assertEquals("This user with id=1 does not exist", e.getMessage());
    }

    @Test
    void getAllByOwnerStatusPAST() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingService.getAllByOwner(user.getId(), PAST, Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDtoResponse bookingDto = bookingService.updateStatus(0, user.getId(), true);
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getAllByOwnerStatusCURRENT() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingService.getAllByOwner(user.getId(), CURRENT, Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDtoResponse bookingDto = bookingService.updateStatus(0, user.getId(), true);
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getAllByOwnerStatusFUTURE() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingService.getAllByOwner(user.getId(), FUTURE, Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDtoResponse bookingDto = bookingService.updateStatus(0, user.getId(), true);
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getAllByOwnerStatusWAITING() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingService.getAllByOwner(user.getId(), WAITING, Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDtoResponse bookingDto = bookingService.updateStatus(0, user.getId(), true);
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getAllByOwnerStatusREJECTED() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingService.getAllByOwner(user.getId(), REJECTED, Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDtoResponse bookingDto = bookingService.updateStatus(0, user.getId(), true);
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getAllByBookerStatusPAST() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingService.getAllByBooker(user.getId(), PAST, Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDtoResponse bookingDto = bookingService.updateStatus(0, user.getId(), true);
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getAllByBookerStatusCURRENT() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingService.getAllByBooker(user.getId(), CURRENT, Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDtoResponse bookingDto = bookingService.updateStatus(0, user.getId(), true);
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getAllByBookerStatusFUTURE() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingService.getAllByBooker(user.getId(), FUTURE, Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDtoResponse bookingDto = bookingService.updateStatus(0, user.getId(), true);
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getAllByBookerStatusWAITING() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingService.getAllByBooker(user.getId(), WAITING, Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDtoResponse bookingDto = bookingService.updateStatus(0, user.getId(), true);
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getAllByBookerStatusREJECTED() {
        User user = new User(1L, "name1", "email1@gmail.com");
        User user2 = new User(2L, "name2", "email2@gmail.com");
        Item item = new Item(1L, "item_name", "item_description", true, user, null);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(item.getId())
                .build();
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDtoRequest, user, item));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingMapper.toBooking(bookingDtoRequest, user2, item)));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingService.getAllByBooker(user.getId(), REJECTED, Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDtoResponse bookingDto = bookingService.updateStatus(0, user.getId(), true);
        assertEquals(bookingDtoRequest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoRequest.getEnd(), bookingDto.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }
}