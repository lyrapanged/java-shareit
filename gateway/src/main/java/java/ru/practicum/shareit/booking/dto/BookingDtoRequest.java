package java.ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.ru.practicum.shareit.util.StartBeforeEnd;
import java.time.LocalDateTime;

@Value
@Builder
@StartBeforeEnd
public class BookingDtoRequest {

    @FutureOrPresent
    LocalDateTime start;
    @Future
    LocalDateTime end;
    @NotNull
    Long itemId;
}
