package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    UserDto create(UserDto userDto);

    UserDto update(int id, UserDto userDto);

    List<UserDto> getAllUsers();

    Optional<User> get(int id);

    Optional<UserDto> delete(int id);
}
