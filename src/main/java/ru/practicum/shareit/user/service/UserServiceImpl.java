package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.UserMapper.fromUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(fromUserDto(userDto)));
    }

    @Override
    @Transactional
    public UserDto update(long id, UserDto userDto) {
        User user = getUserOrThrow(id);
        UserMapper.fromUserDtoToUpdate(userDto, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto get(long id) {
        return UserMapper.toUserDto(getUserOrThrow(id));
    }

    @Override
    @Transactional
    public void delete(long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException | IllegalArgumentException exception) {
            throw new NotFoundException("user with id=" + id);
        }
    }

    private User getUserOrThrow(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id = " + id));
    }
}
