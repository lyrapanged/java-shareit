package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    User owner = new User(1L, "owner", "owner@mail");
    private final ItemRequest request = new ItemRequest(1L, "text", owner, LocalDateTime.now());
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .build();

    private final ItemRequestDtoResponse requestDto = ItemRequestMapper.toItemRequestDtoResponse(request);
    private final ItemRequestDtoResponseWithItems itemRequestDtoResponseWithItems =
            ItemRequestMapper.toItemRequestDtoResponseWithItems(request, List.of(itemDto));

    @SneakyThrows
    @Test
    void create() {
        when(itemRequestService.create(any(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void getByOwner() {
        when(itemRequestService.getByOwner(anyLong())).thenReturn(List.of(itemRequestDtoResponseWithItems));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description",
                        is(itemRequestDtoResponseWithItems.getDescription()), String.class))
                .andExpect(jsonPath("$[0].id",
                        is(itemRequestDtoResponseWithItems.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void getAll() {
        when(itemRequestService.getAll(anyLong(), any())).thenReturn(List.of(itemRequestDtoResponseWithItems));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoResponseWithItems.getDescription()), String.class))
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoResponseWithItems.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void get() {
        when(itemRequestService.get(anyLong(), anyLong())).thenReturn(itemRequestDtoResponseWithItems);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequestDtoResponseWithItems.getDescription()), String.class))
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponseWithItems.getId()), Long.class));
    }
}