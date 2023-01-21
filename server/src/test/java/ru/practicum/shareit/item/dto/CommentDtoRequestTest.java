package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoRequestTest {

    @Autowired
    private JacksonTester<CommentDtoRequest> json;

    @Test
    @SneakyThrows
    void commentDtoRequestTest() {
        CommentDtoRequest bar = CommentDtoRequest.builder()
                .text("text")
                .build();
        JsonContent<CommentDtoRequest> testJson = json.write(bar);
        assertThat(testJson).extractingJsonPathStringValue("$.text")
                .isEqualTo(bar.getText());
    }
}