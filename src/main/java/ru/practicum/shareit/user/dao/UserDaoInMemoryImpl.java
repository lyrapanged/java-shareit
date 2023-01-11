package ru.practicum.shareit.user.dao;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDaoInMemoryImpl implements UserDao {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;


    @Override
    public User create(UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(long id, UserDto userDto) {
        if (users.get(id) == null) {
            throw new NotFoundException("User with email" + userDto.getEmail());
        }
        User user = users.get(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> delete(long id) {
        return Optional.ofNullable(users.remove(id));
    }
}
