package ru.yandex.practicum.catsgram.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.dal.dto.UserDto;
import ru.yandex.practicum.catsgram.model.User;
import ru.yandex.practicum.catsgram.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public Optional<UserDto> findUserById(@PathVariable final Long userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Optional<UserDto> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping
    public Optional<UserDto> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
}
