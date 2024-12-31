package ru.yandex.practicum.catsgram.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.UserRowMapper;
import ru.yandex.practicum.catsgram.exception.EmptyResultDataAccessException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository extends BaseRepository<User>{
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        log.debug("UserRepository(findAll)");
        return findMany(FIND_ALL_QUERY);
    }

    public void addUser(User user) {
        log.debug("добавления данных в таблицу {}",user.toString());
        jdbc.update("INSERT INTO users(username,email,password,registration_date) VALUES(?,?,?,?);",
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                LocalDate.ofInstant(user.getRegistrationDate(), ZoneId.systemDefault()));
    }
    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }
    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }
    public void updateUser(User user){
        log.debug("обновление данных в таблице");
        jdbc.update("UPDATE users " +
                "SET username = '" + user.getUsername() +
                "', email = '" + user.getEmail() +
                "', password = '" + user.getPassword() +
                "' WHERE id = " + user.getId() + ";");
    }
}