package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Value
@Builder
public class BookingDtoResponse {

    long id;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    BookingStatus status;

}


