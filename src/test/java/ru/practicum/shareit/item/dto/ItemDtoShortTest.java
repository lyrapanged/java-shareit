package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoShortTest {

    @Autowired
    private JacksonTester<ItemDtoShort> json;

    @Test
    @SneakyThrows
    void itemDtoShortTestTest() {
        ItemDtoShort bar = ItemDtoShort.builder()
                .idItem(1L)
                .idRequest(2L)
                .idOwner(3L)
                .name("name")
                .build();
        JsonContent<ItemDtoShort> testJson = json.write(bar);
        assertThat(testJson).extractingJsonPathNumberValue("$.idItem").isEqualTo(bar.getIdItem().intValue());
        assertThat(testJson).extractingJsonPathNumberValue("$.idRequest").isEqualTo(bar.getIdRequest().intValue());
        assertThat(testJson).extractingJsonPathNumberValue("$.idOwner").isEqualTo(bar.getIdOwner().intValue());
        assertThat(testJson).extractingJsonPathStringValue("$.name").isEqualTo(bar.getName());
    }
}