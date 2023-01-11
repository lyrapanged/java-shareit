package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    User create(UserDto userDto);

    User update(long id, UserDto userDto);

    List<User> getAll();

    Optional<User> get(long id);

    Optional<User> delete(long id);
}
