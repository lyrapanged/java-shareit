package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        var builder = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable());
        return item.getItemRequest() != null ? builder.requestId(item.getItemRequest().getId()).build() : builder.build();
    }

    public static Item fromItemDto(ItemDto itemDto, ItemRequest itemRequest) {
        var builder = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable());
        return itemRequest != null ? builder.itemRequest(itemRequest).build() : builder.build();
    }

    public static void updateFromItemDto(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    public static ItemDtoWithBookingDate toItemDtoWithBookingDate(Item item, BookingDtoResponseShort last,
                                                                  BookingDtoResponseShort next, List<CommentDtoResponse> comments) {
        var build = ItemDtoWithBookingDate.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments);
        if (last != null) {
            build.lastBooking(last);
        }
        if (next != null) {
            build.nextBooking(next);
        }
        return build.build();
    }
}
