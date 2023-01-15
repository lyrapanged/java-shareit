package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class CommentDtoRequest {

    @NotBlank
    String text;

    @JsonCreator
    public CommentDtoRequest(String text) {
        this.text = text;
    }
}
