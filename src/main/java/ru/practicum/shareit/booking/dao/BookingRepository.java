package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime startTime,
                                                                                 LocalDateTime endTime);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime endTime);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime startTime);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 ORDER BY b.start DESC ")
    List<Booking> findAllItemOwnerBookings(Long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1  AND b.start< ?2 AND b.end> ?3 ORDER BY b.start DESC ")
    List<Booking> findAllItemOwnerCurrentBookings(Long userId, LocalDateTime startTime,
                                                  LocalDateTime endTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND b.end< ?2 ORDER BY b.start DESC ")
    List<Booking> findAllItemOwnerPastBookings(Long userId, LocalDateTime endTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND b.start> ?2 ORDER BY b.start DESC ")
    List<Booking> findAllItemOwnerFutureBookings(Long userId, LocalDateTime startTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND b.status = ?2 ORDER BY b.start DESC ")
    List<Booking> findAllItemOwnerBookingsByStatus(Long userId, BookingStatus status);

    Optional<Booking> findFirstByItemAndBookerAndEndBefore(Item item, User booker, LocalDateTime maxEnd);

    Optional<Booking> findFirstByItemIdAndStartIsBeforeOrderByStartDesc(long itemId, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStartIsAfterOrderByStart(long itemId, LocalDateTime now);
}
