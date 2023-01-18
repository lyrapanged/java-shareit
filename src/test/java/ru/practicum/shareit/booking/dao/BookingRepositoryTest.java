package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    Item item1;
    Item item2;
    Item item3;
    User user1;
    User user2;
    User user3;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;
    Booking booking1;
    Booking booking2;

    @BeforeEach
    void setup() {
        user1 = userRepository.save(new User(1L, "name1", "email1@gmail.com"));
        user2 = userRepository.save(new User(2L, "name2", "email2@gmail.com"));
        user3 = userRepository.save(new User(3L, "name3", "email3@gmail.com"));
        itemRequest1 = itemRequestRepository.save(new ItemRequest(1L, "requestDescription",
                user3, LocalDateTime.now()));
        itemRequest2 = itemRequestRepository.save(new ItemRequest(2L, "requestDescriptionNew",
                user3, LocalDateTime.now()));
        item1 = itemRepository.save(new Item(1L, "item_name1", "description1", false,
                user1, itemRequest1));
        item2 = itemRepository.save(new Item(2L, "item_name2", "description2", true,
                user2, itemRequest1));
        item3 = itemRepository.save(new Item(3L, "item_name3", "description3", true,
                user2, itemRequest2));

        booking1 = bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2), item1, user3, BookingStatus.WAITING));
        booking2 = bookingRepository.save(new Booking(2L, LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(4), item2, user3, BookingStatus.WAITING));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerIdTest() {
        final List<Booking> bookingList = bookingRepository.findAllByBookerId(user3.getId(),
                Pageable.unpaged());
        assertEquals(2, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem(), bookingList.get(0).getItem());
        assertEquals(booking1.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking1.getStatus(), bookingList.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterTest() {
        final List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(user3.getId(),
                LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged());
        assertEquals(2, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem(), bookingList.get(0).getItem());
        assertEquals(booking1.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking1.getStatus(), bookingList.get(0).getStatus());
    }

    @Test
    void findAllItemOwnerBookings() {
        final List<Booking> bookingList = bookingRepository.findAllItemOwnerBookings(user1.getId(), Pageable.unpaged());
        assertEquals(1, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem(), bookingList.get(0).getItem());
        assertEquals(booking1.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking1.getStatus(), bookingList.get(0).getStatus());
        assertEquals(user1.getId(), bookingList.get(0).getItem().getOwner().getId());
    }

    @Test
    void findAllItemOwnerCurrentBookings() {
        final List<Booking> bookingList = bookingRepository.findAllItemOwnerCurrentBookings(user1.getId(),
                LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem(), bookingList.get(0).getItem());
        assertEquals(booking1.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking1.getStatus(), bookingList.get(0).getStatus());
    }

    @Test
    void findAllItemOwnerPastBookings() {
        booking2.setEnd(LocalDateTime.now().minusDays(1));
        final List<Booking> bookingList = bookingRepository.findAllItemOwnerPastBookings(user2.getId(),
                LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
        assertEquals(booking2.getStart(), bookingList.get(0).getStart());
        assertEquals(booking2.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking2.getItem(), bookingList.get(0).getItem());
        assertEquals(booking2.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking2.getStatus(), bookingList.get(0).getStatus());
    }

    @Test
    void findAllItemOwnerFutureBookings() {
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        booking2.setStart(LocalDateTime.now().plusDays(4));
        final List<Booking> bookingList = bookingRepository.findAllItemOwnerFutureBookings(user2.getId(),
                LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
        assertEquals(booking2.getStart(), bookingList.get(0).getStart());
        assertEquals(booking2.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking2.getItem(), bookingList.get(0).getItem());
        assertEquals(booking2.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking2.getStatus(), bookingList.get(0).getStatus());
    }

    @Test
    void findAllItemOwnerBookingsByStatus() {
        booking2.setStatus(BookingStatus.CANCELED);
        final List<Booking> bookingList = bookingRepository.findAllItemOwnerBookingsByStatus(user2.getId(),
                BookingStatus.CANCELED, Pageable.unpaged());
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
        assertEquals(booking2.getStart(), bookingList.get(0).getStart());
        assertEquals(booking2.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking2.getItem(), bookingList.get(0).getItem());
        assertEquals(booking2.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking2.getStatus(), bookingList.get(0).getStatus());
    }

}