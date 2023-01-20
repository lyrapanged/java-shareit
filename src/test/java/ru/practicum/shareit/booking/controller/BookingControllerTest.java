package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    BookingServiceImpl bookingService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("ItemDto1")
            .description("DescriptionItemDto1")
            .available(true)
            .requestId(1L)
            .build();
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("UserName1")
            .email("username1@gmail.com")
            .build();
    private final BookingDtoResponse bookingDto = BookingDtoResponse.builder()
            .id(1L)
            .start(LocalDateTime.now().plusMinutes(11))
            .end(LocalDateTime.now().plusMinutes(22))
            .item(itemDto)
            .booker(userDto)
            .status(BookingStatus.WAITING)
            .build();

    private final BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
            .start(LocalDateTime.now().plusMinutes(11))
            .end(LocalDateTime.now().plusMinutes(22))
            .itemId(itemDto.getId())
            .build();


    @Test
    @SneakyThrows
    void create_StatusIsOk() {
        when(bookingService.create(any(), anyLong())).thenReturn(bookingDto);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void create_BadBookingDto_ThenTrow_BadRequest() {
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(null)
                .build();
        when(bookingService.create(any(), anyLong())).thenReturn(bookingDto);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void create_BadHeaderUserId_ThenTrow_BadRequest() {
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .itemId(null)
                .build();
        when(bookingService.create(any(), anyLong())).thenReturn(bookingDto);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "asd")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateStatus_StatusIsOk() {
        BookingDtoResponse bookingDtoApprove = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoApprove);
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void update_BadParamApprove_ThenThrow_BadRequest() {
        BookingDtoResponse bookingDtoApprove = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoApprove);
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "trrrrrrueeee")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void get_ById_StatusIsOk() {
        when(bookingService.get(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void get_BadHeaderUserId_ThenTrow_BadRequest() {
        when(bookingService.get(anyLong(), anyLong())).thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1L")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getAllByBooker_StatusIsOk() {
        UserDto userDto2 = UserDto.builder()
                .id(1L)
                .name("UserName1")
                .email("username1@gmail.com")
                .build();
        BookingDtoResponse bookingDto2 = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .item(itemDto)
                .booker(userDto2)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.getAllByBooker(anyLong(), any(), any())).thenReturn(List.of(bookingDto, bookingDto2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("from", "0")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].item.id", is(bookingDto2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].booker.id", is(bookingDto2.getBooker().getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void getAllByBooker_BadParamSize_ThenTrow_BadRequest() {
        UserDto userDto2 = UserDto.builder()
                .id(1L)
                .name("UserName1")
                .email("username1@gmail.com")
                .build();
        BookingDtoResponse bookingDto2 = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .item(itemDto)
                .booker(userDto2)
                .status(BookingStatus.WAITING)
                .build();
        when(bookingService.getAllByBooker(anyLong(), any(), any())).thenReturn(List.of(bookingDto, bookingDto2));
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "-5")
                        .param("from", "0")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getAllByBooker_BadParamFrom_ThenTrow_BadRequest() {
        UserDto userDto2 = UserDto.builder()
                .id(1L)
                .name("UserName1")
                .email("username1@gmail.com")
                .build();
        BookingDtoResponse bookingDto2 = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .item(itemDto)
                .booker(userDto2)
                .status(BookingStatus.WAITING)
                .build();
        when(bookingService.getAllByBooker(anyLong(), any(), any())).thenReturn(List.of(bookingDto, bookingDto2));
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("from", "-389")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getAllByBooker_BadParamState_ThenTrow_BadRequest() {
        UserDto userDto2 = UserDto.builder()
                .id(1L)
                .name("UserName1")
                .email("username1@gmail.com")
                .build();
        BookingDtoResponse bookingDto2 = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(11))
                .end(LocalDateTime.now().plusMinutes(22))
                .item(itemDto)
                .booker(userDto2)
                .status(BookingStatus.WAITING)
                .build();
        when(bookingService.getAllByBooker(anyLong(), any(), any())).thenReturn(List.of(bookingDto, bookingDto2));
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("from", "389")
                        .param("state", "FUTURRREEEE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getAllByOwner_StatusIsOk() {
        when(bookingService.getAllByOwner(anyLong(), any(), any())).thenReturn(Collections
                .singletonList(bookingDto));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("from", "0")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void getAllByOwner_BadParamSize_ThenThrow_BadRequest() {
        when(bookingService.getAllByOwner(anyLong(), any(), any())).thenReturn(Collections
                .singletonList(bookingDto));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "-25")
                        .param("from", "0")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getAllByOwner_BadParamFrom_ThenThrow_BadRequest() {
        when(bookingService.getAllByOwner(anyLong(), any(), any())).thenReturn(Collections
                .singletonList(bookingDto));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "25")
                        .param("from", "-23")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getAllByOwner_BadParamState_ThenThrow_BadRequest() {
        when(bookingService.getAllByOwner(anyLong(), any(), any())).thenReturn(Collections
                .singletonList(bookingDto));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "25")
                        .param("from", "23")
                        .param("state", "ALL9")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getAllByOwner_BadHeaderUserId_ThenThrow_BadRequest() {
        when(bookingService.getAllByOwner(anyLong(), any(), any())).thenReturn(Collections
                .singletonList(bookingDto));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1L")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "25")
                        .param("from", "23")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}