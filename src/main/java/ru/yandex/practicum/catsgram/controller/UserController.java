package ru.yandex.practicum.catsgram.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.dal.dto.UserDto;
import ru.yandex.practicum.catsgram.model.User;
import ru.yandex.practicum.catsgram.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public User findUserById(@PathVariable final Long userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/")
    public Collection<UserDto> getUsers() {
        return userService.getUsers();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
}
