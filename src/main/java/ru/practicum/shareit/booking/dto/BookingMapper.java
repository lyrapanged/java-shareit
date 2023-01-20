package ru.practicum.shareit.booking.dto;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class BookingMapper {

    public static BookingDtoResponse toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        var builder = BookingDtoResponse.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .status(booking.getStatus())
                .booker(UserMapper.toUserDto(booking.getBooker()));
        return booking.getId() != null ? builder.id(booking.getId()).build() : builder.build();

    }

    public static Booking toBooking(BookingDtoRequest bookingDtoRequest, User booker, Item item) {
        return Booking.builder()
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDtoResponseShort toBookingDtoShort(Booking booking) {
        return BookingDtoResponseShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
