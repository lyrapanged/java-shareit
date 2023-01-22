package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.X_SHARER_USER_ID;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid BookingDtoRequest bookingDtoRequest,
                                         @RequestHeader(X_SHARER_USER_ID) long bookerId) {
        log.info("Creating booking userId={}", bookerId);
        return bookingClient.create(bookingDtoRequest, bookerId);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> updateStatus(@PathVariable Long bookingId, @RequestHeader(X_SHARER_USER_ID) long userId,
                                               @RequestParam(name = "approved") boolean isApprove) {
        log.info("Updating status bookingId={}", bookingId);
        return bookingClient.updateStatus(bookingId, userId, isApprove);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> get(@PathVariable long bookingId, @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Getting booking id={}", bookingId);
        return bookingClient.get(bookingId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllByBooker(@RequestHeader(X_SHARER_USER_ID) long ownerId,
                                                 @RequestParam(defaultValue = "ALL") String stateParam,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                 Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10")
                                                 Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Getting all booking with idOwner={}", ownerId);
        return bookingClient.getAllByBooker(ownerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(X_SHARER_USER_ID) long ownerId,
                                                @RequestParam(defaultValue = "ALL") BookingState state,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10")
                                                Integer size) {
        log.info("Getting all booking with idOwner={}", ownerId);
        return bookingClient.getAllByOwner(ownerId, state, from, size);
    }
}
