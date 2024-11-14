package ru.yandex.practicum.catsgram.service;

import ch.qos.logback.classic.Level;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<String, User> users = new HashMap<>();

    public Collection<User> getUsers() {
        return users.values();
    }

    public Optional<User> findUserById(Long id){
        return users.values().stream()
                .filter(userId -> userId.getId().equals(id))
                .findFirst();
    }

    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Email должен быть указан");
        }
        if (users.containsKey(user.getEmail())) {
            throw new DuplicatedDataException("Этот Email уже используется");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getEmail(), user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("ID должен быть указан");
        }
        Optional<User> optionalOldUser = users.values().stream()
                .filter(user1 -> user1.getId().equals(user.getId()))
                .findFirst();
        User oldUser;
        String email;
        if (optionalOldUser.isPresent()) {
            oldUser = optionalOldUser.get();
            email = oldUser.getEmail();
        } else {
            throw new NotFoundException("пользователь с указанным ID не найден");
        }
        if (user.getEmail() != null && users.containsKey(user.getEmail())) {
            throw new DuplicatedDataException("Этот Email уже используется");
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        if(user.getUsername() != null){
            oldUser.setUsername(user.getUsername());
        }
        if(user.getPassword() != null){
            oldUser.setPassword(user.getPassword());
        }
        users.remove(email);
        users.put(oldUser.getEmail(), oldUser);
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
