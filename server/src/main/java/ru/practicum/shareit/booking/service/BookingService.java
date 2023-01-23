package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse create(BookingDtoRequest bookingDtoRequest, long bookerId);

    BookingDtoResponse updateStatus(long bookingId, long userId, boolean isApprove);

    BookingDtoResponse get(long bookingId, long userId);

    List<BookingDtoResponse> getAllByBooker(long ownerId, BookingState state, Pageable pageable);

    List<BookingDtoResponse> getAllByOwner(long ownerId, BookingState state, Pageable pageable);
}
