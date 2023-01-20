package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoWithBookingDateTest {

    @Autowired
    private JacksonTester<ItemDtoWithBookingDate> json;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    @SneakyThrows
    void itemDtoWithBookingDateTest() {
        BookingDtoResponseShort lastBooking = BookingDtoResponseShort.builder()
                .id(12L)
                .bookerId(133L)
                .build();
        BookingDtoResponseShort nextBooking = BookingDtoResponseShort.builder()
                .id(1111L)
                .bookerId(2323L)
                .build();
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder()
                .id(1L)
                .text("text")
                .authorName("name")
                .created(LocalDateTime.of(2033, 1, 2, 3, 4))
                .build();
        ItemDtoWithBookingDate baz = ItemDtoWithBookingDate.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(commentDtoResponse))
                .build();
        JsonContent<ItemDtoWithBookingDate> testJson = json.write(baz);
        assertThat(testJson).extractingJsonPathNumberValue("$.id").isEqualTo(baz.getId().intValue());
        assertThat(testJson).extractingJsonPathStringValue("$.name").isEqualTo(baz.getName());
        assertThat(testJson).extractingJsonPathBooleanValue("$.available").isEqualTo(baz.getAvailable());
        assertThat(testJson).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo((int) baz.getLastBooking().getId());
        assertThat(testJson).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo((int) baz.getLastBooking().getBookerId());
        assertThat(testJson).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo((int) baz.getNextBooking().getId());
        assertThat(testJson).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo((int) baz.getNextBooking().getBookerId());
        assertThat(testJson).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(baz.getComments().get(0).getId().intValue());
        assertThat(testJson).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(baz.getComments().get(0).getText());
        assertThat(testJson).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo(baz.getComments().get(0).getAuthorName());
        assertThat(testJson).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(baz.getComments().get(0).getCreated().format(formatter));
    }
}