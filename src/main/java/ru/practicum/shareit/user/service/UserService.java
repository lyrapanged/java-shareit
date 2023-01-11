package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    public UserDto create(UserDto userDto) {
        checkUniqueEmail(userDto.getEmail());
        return UserMapper.toUserDto(userDao.create(userDto));
    }

    public UserDto update(long id, UserDto userDto) {
        if (!getOrThrow(id).getEmail().equals(userDto.getEmail())) {
            checkUniqueEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userDao.update(id, userDto));
    }

    public List<UserDto> getAll() {
        return userDao.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto get(long id) {
        return UserMapper.toUserDto(getOrThrow(id));
    }

    public void delete(long id) {
        if (userDao.delete(id).isEmpty()) {
            throw new NotFoundException("User with id = " + id);
        }
    }

    private void checkUniqueEmail(String email) {
        boolean isUnique = userDao.getAll().stream()
                .map(User::getEmail)
                .noneMatch(userEmail -> userEmail.equals(email));
        if (!isUnique) {
            throw new EmailAlreadyExistException(email);
        }
    }

    private User getOrThrow(long id) {
        return userDao.get(id).orElseThrow(() -> new NotFoundException("User with id = " + id));
    }
}
