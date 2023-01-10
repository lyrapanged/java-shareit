package ru.practicum.shareit.user.dao;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDaoInMemoryImpl implements UserDao {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;


    @Override
    public UserDto create(UserDto userDto) {
        if (users.get(userDto.getId()) != null) {
            throw new EmailAlreadyExistException(userDto.getEmail());
        }
        User user = UserMapper.fromUserDto(userDto);
        user.setId(++id);
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(int id, UserDto userDto) {
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
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    @Override
    public Optional<User> get(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<UserDto> delete(int id) {
        return Optional.ofNullable(UserMapper.toUserDto(users.remove(id)));
    }
}
