package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("name")
            .email("email@gmail.com")
            .build();

    @SneakyThrows
    @Test
    void create_isOk() {
        when(userService.create(any())).thenReturn(userDto);
        mockMvc.perform(post("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void create_BadBodyEmptyName_ThenTrow_BadRequest() {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("")
                .email("email@gmail.com")
                .build();
        when(userService.create(any())).thenReturn(userDto1);
        mockMvc.perform(post("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_BadBodyWrongEmail_ThenTrow_BadRequest() {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("ad")
                .email("emailgmail.com")
                .build();
        when(userService.create(any())).thenReturn(userDto1);
        mockMvc.perform(post("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @SneakyThrows
    @Test
    void getAll_isOk() {
        UserDto userDto1 = UserDto.builder()
                .id(2L)
                .name("name2")
                .email("email2@gmail.com")
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(3L)
                .name("name3")
                .email("email3@gmail.com")
                .build();
        List<UserDto> userList = List.of(userDto, userDto1, userDto2);
        when(userService.getAll()).thenReturn(userList);
        mockMvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @SneakyThrows
    @Test
    void get_isOk() {
        when(userService.get(anyLong())).thenReturn(userDto);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void update_isOK() {
        UserDto userDto = UserDto.builder()
                .name("name")
                .email("email@gmail.com")
                .build();
        when(userService.update(anyLong(), any())).thenReturn(userDto);
        mockMvc.perform(patch("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void update_BadBody_EmptyName_ThenTrow_BadRequest() {
        UserDto userDto = UserDto.builder()
                .name("")
                .email("email@gmail.com")
                .build();
        when(userService.update(anyLong(), any())).thenReturn(userDto);
        mockMvc.perform(patch("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_BadBody_EmptyEmail_ThenTrow_BadRequest() {
        UserDto userDto = UserDto.builder()
                .name("name")
                .email("")
                .build();
        when(userService.update(anyLong(), any())).thenReturn(userDto);
        mockMvc.perform(patch("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_BadBody_IdNotNull_ThenTrow_BadRequest() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("aaaa@gmail.com")
                .build();
        when(userService.update(anyLong(), any())).thenReturn(userDto);
        mockMvc.perform(patch("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void delete_isOk() {
        doNothing().when(userService).delete(anyLong());
        mockMvc.perform(delete("/users/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}