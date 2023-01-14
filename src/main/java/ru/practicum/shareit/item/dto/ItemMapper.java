package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item fromItemDto(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
    public static void updateFromItemDto(ItemDto itemDto, Item item) {
        if(itemDto.getName() != null){
            item.setName(itemDto.getName());
        }
        if(itemDto.getDescription() != null){
            item.setDescription(itemDto.getDescription());
        }
        if(itemDto.getAvailable() != null){
            item.setAvailable(itemDto.getAvailable());
        }
    }

    public static ItemDtoWithBookingDate toItemDtoWithBookingDate(Item item, BookingDtoResponse last
            , BookingDtoResponse next, List<CommentDtoResponse> comments){
        var build = ItemDtoWithBookingDate.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments);
        if(last != null){
            build.lastBooking(last);
        }
        if (next != null){
            build.nextBooking(next);
        }
        return build.build();
    }
}
