package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
@Value
@Builder
@Getter
@Setter
public class BookingDtoResponseShort {
        long id;
        long bookerId;

}
