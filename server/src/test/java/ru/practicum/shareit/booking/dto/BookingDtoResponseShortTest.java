package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoResponseShortTest {

    @Autowired
    private JacksonTester<BookingDtoResponseShort> json;

    @Test
    @SneakyThrows
    void bookingDtoResponseShortTest() {
        BookingDtoResponseShort foo = BookingDtoResponseShort.builder()
                .bookerId(1)
                .id(1)
                .build();
        JsonContent<BookingDtoResponseShort> testJson = json.write(foo);
        assertThat(testJson).extractingJsonPathNumberValue("$.bookerId").isEqualTo((int) ((foo.getBookerId())));
        assertThat(testJson).extractingJsonPathNumberValue("$.id").isEqualTo((int) ((foo.getId())));
    }
}