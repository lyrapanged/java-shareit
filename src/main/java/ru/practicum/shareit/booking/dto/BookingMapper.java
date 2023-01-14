package ru.practicum.shareit.booking.dto;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class BookingMapper {

    public static BookingDtoResponse toBookingDto(Booking booking) {
        if(booking == null){
            return null;
        }
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .build();
    }

    public static Booking toBooking(BookingDtoRequest bookingDtoRequest, User booker, Item item) {
        return Booking.builder()
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .booker(booker)
                .item(item)
                .build();
    }
}