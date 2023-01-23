package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class CommentDtoRequest {


    String text;

    @JsonCreator
    public CommentDtoRequest(String text) {
        this.text = text;
    }
}
