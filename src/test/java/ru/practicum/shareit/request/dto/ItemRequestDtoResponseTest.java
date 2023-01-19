package ru.practicum.shareit.request.dto;

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
class ItemRequestDtoResponseTest {

    @Autowired
    private JacksonTester<ItemRequestDtoResponse> json;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    @SneakyThrows
    void itemRequestDtoResponseTest() {
        ItemRequestDtoResponse bar = ItemRequestDtoResponse.builder()
                .id(1)
                .description("description")
                .created(LocalDateTime.of(2033, 1, 2, 3, 4))
                .build();
        JsonContent<ItemRequestDtoResponse> testJson = json.write(bar);
        assertThat(testJson).extractingJsonPathNumberValue("$.id").isEqualTo((int) bar.getId());
        assertThat(testJson).extractingJsonPathStringValue("$.description").isEqualTo(bar.getDescription());
        assertThat(testJson).extractingJsonPathStringValue("$.created")
                .isEqualTo(bar.getCreated().format(formatter));
    }
}