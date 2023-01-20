package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingDate;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .build();
    private final ItemDtoWithBookingDate itemDtoWithBookingDate = ItemDtoWithBookingDate.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .build();


    @Test
    @SneakyThrows
    void create_Status_isOk() {
        when(itemService.create(any(), anyLong())).thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void create_BadHeader_ThenThrow_BadRequest() {
        when(itemService.create(any(), anyLong())).thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "e1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void create_BadBody_ThenThrow_BadRequest() {
        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("item")
                .available(null)
                .build();
        when(itemService.create(any(), anyLong())).thenReturn(itemDto1);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "e1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void update() {
        when(itemService.update(anyLong(), any(), anyLong())).thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void update_BadBody_ThenThrow_BadRequest() {
        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("item")
                .available(null)
                .build();
        when(itemService.update(anyLong(), any(), anyLong())).thenReturn(itemDto1);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "e1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void get_Status_isOk() {
        when(itemService.get(anyLong(), anyLong())).thenReturn(itemDtoWithBookingDate);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void get_BadHeaderUserId_ThenThrow_BadRequest() {
        when(itemService.get(anyLong(), anyLong())).thenReturn(itemDtoWithBookingDate);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", "1L")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getByOwner_Status_isOk() {
        when(itemService.getByOwner(anyLong(), any())).thenReturn(Collections.singletonList(itemDtoWithBookingDate));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("from", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void getByOwner_BadParamSize_ThenTrow_BadRequest() {
        when(itemService.getByOwner(anyLong(), any())).thenReturn(Collections.singletonList(itemDtoWithBookingDate));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "0")
                        .param("from", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getByOwner_BadParamFrom_ThenTrow_BadRequest() {
        when(itemService.getByOwner(anyLong(), any())).thenReturn(Collections.singletonList(itemDtoWithBookingDate));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "3")
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getByOwner_BadHeaderUserID_ThenTrow_BadRequest() {
        when(itemService.getByOwner(anyLong(), any())).thenReturn(Collections.singletonList(itemDtoWithBookingDate));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", "3L")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "3")
                        .param("from", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void search_Status_isOk() {
        when(itemService.search(anyString(), any())).thenReturn(Collections.singletonList(itemDto));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("from", "0")
                        .param("text", "text")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void search_BadParamSize_ThenThrow_BadRequest() {
        when(itemService.search(anyString(), any())).thenReturn(Collections.singletonList(itemDto));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "-5")
                        .param("from", "0")
                        .param("text", "text")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void search_BadParamFrom_ThenThrow_BadRequest() {
        when(itemService.search(anyString(), any())).thenReturn(Collections.singletonList(itemDto));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("from", "-10")
                        .param("text", "text")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createComment_Status_isOk() {
        LocalDateTime time = LocalDateTime.of(2033, 1, 4, 1, 5, 9);
        CommentDtoRequest commentRequestDto = CommentDtoRequest.builder()
                .text("TEXT_TEXT").build();
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder()
                .id(1L)
                .created(time)
                .authorName("name")
                .build();
        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentDtoResponse);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDtoResponse.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDtoResponse.getAuthorName()), String.class))
                .andExpect(jsonPath("$.created", is(commentDtoResponse.getCreated().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.id", is(commentDtoResponse.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void createComment_BadBody_ThenTrow_BadRequest() {
        LocalDateTime time = LocalDateTime.of(2033, 1, 4, 1, 5, 9);
        CommentDtoRequest commentRequestDto = CommentDtoRequest.builder()
                .text("").build();
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder()
                .id(1L)
                .created(time)
                .authorName("name")
                .build();
        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentDtoResponse);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}