package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void itemDtoTestTest() {
        ItemDto bar = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(2L)
                .build();
        JsonContent<ItemDto> testJson = json.write(bar);
        assertThat(testJson).extractingJsonPathNumberValue("$.id").isEqualTo(bar.getId().intValue());
        assertThat(testJson).extractingJsonPathStringValue("$.name").isEqualTo(bar.getName());
        assertThat(testJson).extractingJsonPathStringValue("$.description").isEqualTo(bar.getDescription());
        assertThat(testJson).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(bar.getAvailable());
        assertThat(testJson).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(bar.getRequestId().intValue());
    }
}