package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class BookingDtoRequestTest {

    @Autowired
    private JacksonTester<BookingDtoRequest> json;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    @SneakyThrows
    void bookingDtoRequestTest() {
        BookingDtoRequest foo = BookingDtoRequest.builder()
                .start(LocalDateTime.of(2033, 1, 2, 3, 4))
                .end(LocalDateTime.of(2034, 1, 2, 3, 4))
                .itemId(1L)
                .build();
        JsonContent<BookingDtoRequest> testJson = json.write(foo);
        assertThat(testJson).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(foo.getItemId().intValue());
        assertThat(testJson).extractingJsonPathStringValue("$.start")
                .isEqualTo(foo.getStart().format(formatter));
        assertThat(testJson).extractingJsonPathStringValue("$.end")
                .isEqualTo(foo.getEnd().format(formatter));
    }
}