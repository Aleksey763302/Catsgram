package ru.yandex.practicum.catsgram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.dto.UserDto;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.maper.UserMapper;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    public UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getUsers() {
        log.debug("getUsers");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public User findUserById(Long id) {
        log.debug("findUserById");
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Email должен быть указан");
        }
        log.debug("создан пользователь");
        user.setRegistrationDate(Instant.now());
        log.debug("обращение к UserRepository {}",user);
        userRepository.addUser(user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("ID должен быть указан");
        }
        userRepository.updateUser(user);
        log.debug("обновление успешно");
        return user;
    }

}