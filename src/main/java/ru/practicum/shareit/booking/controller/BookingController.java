package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse create(
            @RequestBody @Valid BookingDtoRequest bookingDtoRequest,
            @RequestHeader(X_SHARER_USER_ID) long bookerId) {
        log.info("Creating booking userId={}", bookerId);
        return bookingService.create(bookingDtoRequest, bookerId);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoResponse updateStatus(
            @PathVariable Long bookingId,
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestParam(name = "approved") boolean isApprove) {
        log.info("Updating status bookingId={}", bookingId);
        return bookingService.updateStatus(bookingId, userId, isApprove);
    }

    @GetMapping("{bookingId}")
    public BookingDtoResponse get(@PathVariable long bookingId,
                                  @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Getting booking id={}", bookingId);
        return bookingService.get(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDtoResponse> getAllByBooker(
            @RequestHeader(X_SHARER_USER_ID) long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Getting all booking with idOwner={}", ownerId);
        return bookingService.getAllByBooker(ownerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllByOwner(
            @RequestHeader(X_SHARER_USER_ID) long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Getting all booking with idOwner={}", ownerId);
        return bookingService.getAllByOwner(ownerId, state);
    }
}
