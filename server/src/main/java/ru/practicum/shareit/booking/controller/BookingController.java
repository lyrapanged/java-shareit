package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingState;

import java.util.List;

import static ru.practicum.shareit.util.Constants.SORT_BY_START_DESC;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse create(@RequestBody BookingDtoRequest bookingDtoRequest,
                                     @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Creating booking userId={}", bookerId);
        return bookingService.create(bookingDtoRequest, bookerId);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoResponse updateStatus(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(name = "approved") boolean isApprove) {
        log.info("Updating status bookingId={}", bookingId);
        return bookingService.updateStatus(bookingId, userId, isApprove);
    }

    @GetMapping("{bookingId}")
    public BookingDtoResponse get(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting booking id={}", bookingId);
        return bookingService.get(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDtoResponse> getAllByBooker(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                   @RequestParam(defaultValue = "ALL") BookingState state,
                                                   @RequestParam(name = "from", defaultValue = "0")
                                                   Integer from,
                                                   @RequestParam(name = "size", defaultValue = "10")
                                                   Integer size) {
        int page = from / size;
        final Pageable pageable = PageRequest.of(page, size, SORT_BY_START_DESC);
        log.info("Getting all booking with idOwner={}", ownerId);
        return bookingService.getAllByBooker(ownerId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                  @RequestParam(defaultValue = "ALL") BookingState state,
                                                  @RequestParam(name = "from", defaultValue = "0")
                                                  Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10")
                                                  Integer size) {
        log.info("Getting all booking with idOwner={}", ownerId);
        int page = from / size;
        final Pageable pageable = PageRequest.of(page, size, SORT_BY_START_DESC);
        return bookingService.getAllByOwner(ownerId, state, pageable);
    }
}
