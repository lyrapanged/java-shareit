package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserDto create(UserDto userDto) {
        checkUniqueEmail(userDto.getEmail());
        return userDao.create(userDto);
    }

    public UserDto update(int id, UserDto userDto) {
        checkUniqueEmail(userDto.getEmail());
        return userDao.update(id, userDto);
    }

    public List<UserDto> getAllUsers() {
        return userDao.getAllUsers();
    }

    public UserDto get(int id) {
        return UserMapper.toUserDto(userDao.get(id).orElseThrow(() -> new NotFoundException("User with id = " + id)));
    }

    public void delete(int id) {
        if (userDao.delete(id) == null) {
            throw new NotFoundException("User with id = " + id);
        }
    }

    private void checkUniqueEmail(String email) {
        boolean isUnique = userDao.getAllUsers().stream()
                .map(UserDto::getEmail)
                .noneMatch(userEmail -> userEmail.equals(email));
        if (!isUnique) {
            throw new EmailAlreadyExistException(email);
        }
    }
}
