package ru.yandex.practicum.catsgram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.dto.UserDto;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.EmptyResultDataAccessException;
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
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Optional<UserDto> findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper::mapToUserDto);
    }

    public Optional<UserDto> createUser(User user) {
        if (Objects.isNull(user.getEmail()) || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Email должен быть указан");
        }
        user.setRegistrationDate(Instant.now());
        Optional<User> userOptional = userRepository.addUser(user);
        if (userOptional.isEmpty()) {
            throw new EmptyResultDataAccessException("данные не были добавлены");
        }
        return Optional.of(UserMapper.mapToUserDto(userOptional.get()));
    }

    public Optional<UserDto> updateUser(User user) {
        if (Objects.isNull(user.getId())) {
            throw new ConditionsNotMetException("ID должен быть указан");
        }
        Optional<User> userOptional;
        try {
            userOptional = userRepository.updateUser(user);
        } catch (RuntimeException e) {
            log.debug("ошибка при обновлении {}", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        if (userOptional.isEmpty()) {
            throw new EmptyResultDataAccessException("данные не были обновлены");
        }
        return Optional.of(UserMapper.mapToUserDto(userOptional.get()));
    }
}