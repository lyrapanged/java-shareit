package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoResponseWithItemsTest {

    @Autowired
    JacksonTester<ItemRequestDtoResponseWithItems> json;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    @SneakyThrows
    void itemRequestDtoResponseWithItemsTest() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .requestId(2L)
                .available(true)
                .description("description")
                .name("name")
                .build();
        ItemRequestDtoResponseWithItems baz = ItemRequestDtoResponseWithItems.builder()
                .id(1L)
                .description("description111")
                .created(LocalDateTime.of(2034, 1, 2, 3, 4))
                .items(List.of(itemDto))
                .build();
        JsonContent<ItemRequestDtoResponseWithItems> testJson = json.write(baz);
        assertThat(testJson).extractingJsonPathNumberValue("$.id").isEqualTo((int) baz.getId());
        assertThat(testJson).extractingJsonPathStringValue("$.description").isEqualTo(baz.getDescription());
        assertThat(testJson).extractingJsonPathStringValue("$.created")
                .isEqualTo(baz.getCreated().format(formatter));
        assertThat(testJson).extractingJsonPathNumberValue(("$.items[0].id"))
                .isEqualTo(baz.getItems().get(0).getId().intValue());
        assertThat(testJson).extractingJsonPathNumberValue(("$.items[0].requestId"))
                .isEqualTo(baz.getItems().get(0).getRequestId().intValue());
        assertThat(testJson).extractingJsonPathBooleanValue(("$.items[0].available"))
                .isEqualTo(baz.getItems().get(0).getAvailable());
        assertThat(testJson).extractingJsonPathStringValue(("$.items[0].description"))
                .isEqualTo(baz.getItems().get(0).getDescription());
        assertThat(testJson).extractingJsonPathStringValue(("$.items[0].name"))
                .isEqualTo(baz.getItems().get(0).getName());
    }
}