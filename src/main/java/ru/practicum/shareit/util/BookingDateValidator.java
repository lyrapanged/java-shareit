package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookingDateValidator implements ConstraintValidator<StartBeforeEnd, BookingDtoRequest> {
    @Override
    public void initialize(StartBeforeEnd constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDtoRequest bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
