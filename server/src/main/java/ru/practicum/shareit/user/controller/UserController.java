package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Creating user with email{}", userDto.getEmail());
        return userService.create(userDto);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Getting all Users");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable("userId") long id) {
        log.info("Getting user by id={}", id);
        return userService.get(id);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable("userId") long id,
                          @RequestBody UserDto userDto) {
        log.info("Updating user id={}", id);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") long id) {
        log.info("Deleting user id={}", id);
        userService.delete(id);
    }
}
