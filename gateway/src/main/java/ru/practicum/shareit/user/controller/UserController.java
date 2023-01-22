package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.user.dto.UserDto.AdvancedConstraint;
import static ru.practicum.shareit.user.dto.UserDto.BasicConstraint;

@Controller
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping()
    public ResponseEntity<Object> create(@Validated(AdvancedConstraint.class) @RequestBody UserDto userDto) {
        log.info("Creating user with email{}", userDto.getEmail());
        return userClient.create(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Getting all Users");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable("userId") long id) {
        log.info("Getting user by id={}", id);
        return userClient.get(id);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") long id,
                                         @Validated(BasicConstraint.class) @RequestBody UserDto userDto) {
        log.info("Updating user id={}", id);
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") long id) {
        log.info("Deleting user id={}", id);
        return userClient.delete(id);
    }
}
