package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;
import static ru.practicum.shareit.util.Constants.SORT_BY_START_DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoResponse create(BookingDtoRequest bookingDtoRequest, long bookerId) {
        User booker = getUserOrThrow(bookerId);
        Item item = getItemOrTrow(bookingDtoRequest.getItemId());
        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("Not today mate");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item must  be available for booking");
        }
        Booking booking = BookingMapper.toBooking(bookingDtoRequest, booker, item);
        booking.setStatus(WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoResponse updateStatus(long bookingId, long userId, boolean isApprove) {
        Booking booking = getBookingOrThrow(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Owner only");
        }
        if (booking.getStatus().equals(WAITING)) {
            booking.setStatus(isApprove ? APPROVED : REJECTED);
        } else {
            throw new ValidationException("Nothing to changing");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDtoResponse get(long bookingId, long userId) {
        Booking booking = getBookingOrThrow(bookingId);
        if (booking.getItem().getOwner().getId() != userId && userId != booking.getBooker().getId()) {
            throw new NotFoundException("No access");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDtoResponse> getAllByBooker(long ownerId, BookingState state) {
        getUserOrThrow(ownerId);
        switch (state) {
            case PAST:
                return convertToBookingDto(bookingRepository
                        .findAllByBookerIdAndEndIsBefore(ownerId, now(), SORT_BY_START_DESC));
            case CURRENT:
                return convertToBookingDto(bookingRepository
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfter(ownerId, now(), now(), SORT_BY_START_DESC));
            case FUTURE:
                return convertToBookingDto(bookingRepository
                        .findAllByBookerIdAndStartIsAfter(ownerId, now(), SORT_BY_START_DESC));
            case WAITING:
                return convertToBookingDto(bookingRepository
                        .findAllByBookerIdAndStatus(ownerId, WAITING, SORT_BY_START_DESC));
            case REJECTED:
                return convertToBookingDto(bookingRepository
                        .findAllByBookerIdAndStatus(ownerId, REJECTED, SORT_BY_START_DESC));
            default:
                return convertToBookingDto(bookingRepository.findAllByBookerIdOrderByStartDesc(ownerId));
        }
    }

    @Override
    public List<BookingDtoResponse> getAllByOwner(long ownerId, BookingState state) {
        getUserOrThrow(ownerId);
        switch (state) {
            case PAST:
                return convertToBookingDto(bookingRepository
                        .findAllItemOwnerPastBookings(ownerId, now(), SORT_BY_START_DESC));
            case CURRENT:
                return convertToBookingDto(bookingRepository
                        .findAllItemOwnerCurrentBookings(ownerId, now(), SORT_BY_START_DESC));
            case FUTURE:
                return convertToBookingDto(bookingRepository
                        .findAllItemOwnerFutureBookings(ownerId, now(), SORT_BY_START_DESC));
            case WAITING:
                return convertToBookingDto(bookingRepository
                        .findAllItemOwnerBookingsByStatus(ownerId, WAITING, SORT_BY_START_DESC));
            case REJECTED:
                return convertToBookingDto(bookingRepository
                        .findAllItemOwnerBookingsByStatus(ownerId, REJECTED, SORT_BY_START_DESC));
            default:
                return convertToBookingDto(bookingRepository
                        .findAllItemOwnerBookings(ownerId, SORT_BY_START_DESC));
        }
    }

    private Booking getBookingOrThrow(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking with id=" + bookingId));
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("booking with id=" + userId));
    }

    private Item getItemOrTrow(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("booking with id=" + itemId));
    }

    private List<BookingDtoResponse> convertToBookingDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).collect(toList());
    }
}
