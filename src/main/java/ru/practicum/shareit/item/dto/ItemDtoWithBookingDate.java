package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;

import java.util.List;

@Value
@Builder
public class ItemDtoWithBookingDate {

    Long id;
    String name;
    String description;
    Boolean available;
    BookingDtoResponseShort lastBooking;
    BookingDtoResponseShort nextBooking;
    List<CommentDtoResponse> comments;
}
