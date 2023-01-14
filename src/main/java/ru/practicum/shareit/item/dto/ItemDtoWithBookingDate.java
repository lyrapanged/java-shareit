package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@Value
@Builder
public class ItemDtoWithBookingDate {

    Long id;
    String name;
    String description;
    Boolean available;
    BookingDtoResponse lastBooking;
    BookingDtoResponse nextBooking;
    List<CommentDtoResponse> comments;
}
