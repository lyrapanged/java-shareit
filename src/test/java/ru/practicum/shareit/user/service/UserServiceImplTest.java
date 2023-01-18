package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    UserService userService;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void create_isOk() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@gamail.com")
                .build();
        when(userRepository.save(any())).thenReturn(UserMapper.fromUserDto(userDto));
        UserDto userDto1 = userService.create(userDto);
        assertEquals(userDto.getId(), userDto1.getId());
        assertEquals(userDto.getName(), userDto1.getName());
        assertEquals(userDto.getEmail(), userDto1.getEmail());
    }

    @Test
    void update_isOk() {
        User user = new User(1L, "mane", "imail@.gmail.com");
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@gamail.com")
                .build();
        when(userRepository.save(any())).thenReturn(UserMapper.fromUserDto(userDto));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        UserDto updatedUserDto = userService.update(userDto.getId(), userDto);
        assertEquals(user.getId(), updatedUserDto.getId());
        assertEquals(user.getName(), updatedUserDto.getName());
        assertEquals(user.getEmail(), updatedUserDto.getEmail());
    }

    @Test
    void getAll_isOk() {
        User user = new User(1L, "mane", "imail@.gmail.com");
        User user2 = new User(2L, "mane1", "imail@.gmail.com");
        List<User> foo = List.of(user, user2);
        when(userRepository.findAll()).thenReturn(foo);
        List<UserDto> bar = userService.getAll();
        assertEquals(foo.size(), bar.size());
        assertEquals(foo.get(0).getId(), bar.get(0).getId());
        assertEquals(foo.get(0).getName(), bar.get(0).getName());
        assertEquals(foo.get(0).getEmail(), bar.get(0).getEmail());
    }

    @Test
    void get_isOk() {
        User user = new User(1L, "mane", "imail@.gmail.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto userDto = userService.get(user.getId());
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void delete_isOk() {
        userService.delete(1);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_isThrow() {
        doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(1L);
        verify(userRepository, never()).deleteById(1L);
    }
}