package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime startTime,
                                                                 LocalDateTime endTime, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long userId, LocalDateTime endTime, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long userId, LocalDateTime startTime, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1")
    List<Booking> findAllItemOwnerBookings(Long userId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE  ?2 BETWEEN b.start AND  b.end AND b.item.owner.id = ?1")
    List<Booking> findAllItemOwnerCurrentBookings(Long userId, LocalDateTime time, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND b.end< ?2")
    List<Booking> findAllItemOwnerPastBookings(Long userId, LocalDateTime endTime, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND b.start> ?2")
    List<Booking> findAllItemOwnerFutureBookings(Long userId, LocalDateTime startTime, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND b.status = ?2")
    List<Booking> findAllItemOwnerBookingsByStatus(Long userId, BookingStatus status, Sort sort);

    Optional<Booking> findFirstByItemAndBookerAndEndBefore(Item item, User booker, LocalDateTime maxEnd);

    Optional<Booking> findFirstByItemAndStartLessThanEqualAndStatusIs(
            Item itemId, LocalDateTime now, BookingStatus status, Sort sort);

    Optional<Booking> findFirstByItemAndStartAfterAndStatusIs(
            Item itemId, LocalDateTime now, BookingStatus status, Sort sort);

    List<Booking> findAllByItemInAndStartLessThanEqualAndStatusIs(
            List<Item> items, LocalDateTime now, BookingStatus status, Sort sort);

    List<Booking> findAllByItemInAndStartAfterAndStatusIs(
            List<Item> items, LocalDateTime now, BookingStatus status, Sort sort);
}
