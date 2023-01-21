package ru.practicum.shareit.item.dto;

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
class CommentDtoResponseTest {

    @Autowired
    private JacksonTester<CommentDtoResponse> json;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    @SneakyThrows
    void commentDtoResponseTest() {
        CommentDtoResponse bar = CommentDtoResponse.builder()
                .created(LocalDateTime.of(2033, 1, 2, 3, 4))
                .authorName("name")
                .text("text")
                .id(1L)
                .build();
        JsonContent<CommentDtoResponse> testJson = json.write(bar);
        assertThat(testJson).extractingJsonPathStringValue("$.created")
                .isEqualTo(bar.getCreated().format(formatter));
        assertThat(testJson).extractingJsonPathStringValue("$.authorName").isEqualTo(bar.getAuthorName());
        assertThat(testJson).extractingJsonPathStringValue("$.text").isEqualTo(bar.getText());
        assertThat(testJson).extractingJsonPathNumberValue("$.id").isEqualTo((bar.getId().intValue()));
    }
}