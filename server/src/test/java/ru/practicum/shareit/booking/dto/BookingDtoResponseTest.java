package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@JsonTest
class BookingDtoResponseTest {

    @Autowired
    private JacksonTester<BookingDtoResponse> json;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    @SneakyThrows
    void bookingDtoResponseTest() {
        UserDto bar = UserDto.builder()
                .name("name")
                .email("email@gmail.com")
                .id(1L)
                .build();
        BookingDtoResponse foo = BookingDtoResponse.builder()
                .start(LocalDateTime.of(2033, 1, 2, 3, 4))
                .end(LocalDateTime.of(2034, 1, 2, 3, 4))
                .id(1)
                .booker(bar)
                .status(APPROVED)
                .build();
        JsonContent<BookingDtoResponse> testJson = json.write(foo);
        assertThat(testJson).extractingJsonPathStringValue("$.start")
                .isEqualTo(foo.getStart().format(formatter));
        assertThat(testJson).extractingJsonPathStringValue("$.end")
                .isEqualTo(foo.getEnd().format(formatter));
        assertThat(testJson).extractingJsonPathNumberValue("$.id").isEqualTo((int) foo.getId());
        assertThat(testJson).extractingJsonPathStringValue("$.status").isEqualTo(foo.getStatus().toString());
        assertThat(testJson).extractingJsonPathStringValue("$.booker.name").isEqualTo(bar.getName());
        assertThat(testJson).extractingJsonPathStringValue("$.booker.email").isEqualTo(bar.getEmail());
        assertThat(testJson).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bar.getId().intValue());
    }
}