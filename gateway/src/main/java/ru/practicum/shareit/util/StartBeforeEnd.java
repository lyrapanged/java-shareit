package ru.practicum.shareit.util;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE_USE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = BookingDateValidator.class)
public @interface StartBeforeEnd {
    String message() default "Start must be before end or not null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
