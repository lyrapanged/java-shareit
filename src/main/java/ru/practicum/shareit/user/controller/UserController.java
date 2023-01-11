package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.user.dto.UserDto.AdvancedConstraint;

@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public UserDto create(@Validated(AdvancedConstraint.class) @RequestBody UserDto userDto) {
        log.info("Creating user with email{}", userDto.getEmail());
        return userService.create(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Getting all Users");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable("userId") int id) {
        log.info("Getting user by id={}", id);
        return userService.get(id);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable("userId") int id, @Valid @RequestBody UserDto userDto) {
        log.info("Updating user id={}", id);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") int id) {
        log.info("Deleting user id={}", id);
        userService.delete(id);
    }
}
