package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
            throw new NotFoundException("id should be equal booker id. Entity");
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
            throw new NotFoundException("Access");
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
            throw new NotFoundException("access");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDtoResponse> getAllByBooker(long ownerId, BookingState state, Pageable pageable) {
        getUserOrThrow(ownerId);
        switch (state) {
            case PAST:
                return convertToBookingDto(bookingRepository.findAllByBookerIdAndEndIsBefore(ownerId, now(), pageable));
            case CURRENT:
                return convertToBookingDto(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(ownerId, now(), now(), pageable));
            case FUTURE:
                return convertToBookingDto(bookingRepository.findAllByBookerIdAndStartIsAfter(ownerId, now(), pageable));
            case WAITING:
                return convertToBookingDto(bookingRepository.findAllByBookerIdAndStatus(ownerId, WAITING, pageable));
            case REJECTED:
                return convertToBookingDto(bookingRepository.findAllByBookerIdAndStatus(ownerId, REJECTED, pageable));
            default:
                return convertToBookingDto(bookingRepository.findAllByBookerId(ownerId, pageable));
        }
    }

    @Override
    public List<BookingDtoResponse> getAllByOwner(long ownerId, BookingState state, Pageable pageable) {
        getUserOrThrow(ownerId);
        switch (state) {
            case PAST:
                return convertToBookingDto(bookingRepository.findAllItemOwnerPastBookings(ownerId, now(), pageable));
            case CURRENT:
                return convertToBookingDto(bookingRepository.findAllItemOwnerCurrentBookings(ownerId, now(), pageable));
            case FUTURE:
                return convertToBookingDto(bookingRepository.findAllItemOwnerFutureBookings(ownerId, now(), pageable));
            case WAITING:
                return convertToBookingDto(bookingRepository.findAllItemOwnerBookingsByStatus(ownerId, WAITING, pageable));
            case REJECTED:
                return convertToBookingDto(bookingRepository.findAllItemOwnerBookingsByStatus(ownerId, REJECTED, pageable));
            default:
                return convertToBookingDto(bookingRepository.findAllItemOwnerBookings(ownerId, pageable));
        }
    }

    private Booking getBookingOrThrow(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("booking with id=" + bookingId));
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("user with id=" + userId));
    }

    private Item getItemOrTrow(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("item with id=" + itemId));
    }

    private List<BookingDtoResponse> convertToBookingDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).collect(toList());
    }
}
