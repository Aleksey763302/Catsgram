package ru.yandex.practicum.catsgram.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = :email";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = :userID";
    private static final String CREATE_USER = "INSERT INTO users(username,email,password,registration_date)" +
            " VALUES(:username,:email,:password,:registrationDate)";
    private static final String UPDATE_USER = "UPDATE users " +
            "SET username = :username, email = :email, password = :password WHERE id = :userID";

    public UserRepository(NamedParameterJdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        SqlParameterSource params = new MapSqlParameterSource();
        return findMany(FIND_ALL_QUERY, params);
    }

    public Optional<User> addUser(User user) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", user.getUsername())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("registrationDate",
                        LocalDate.ofInstant(user.getRegistrationDate(), ZoneId.systemDefault()));
        return findById(insert(CREATE_USER, params));
    }

    public Optional<User> findByEmail(String email) {
        SqlParameterSource params = new MapSqlParameterSource().addValue("email", email);
        return findOne(FIND_BY_EMAIL_QUERY, params);
    }

    public Optional<User> findById(long userId) {
        SqlParameterSource params = new MapSqlParameterSource().addValue("userID", userId);
        return findOne(FIND_BY_ID_QUERY, params);
    }

    public Optional<User> updateUser(User user) throws DataAccessException {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userID",user.getId())
                .addValue("username", user.getUsername())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword());
        if(update(UPDATE_USER,params)){
            return findById(user.getId());
        }
        return Optional.empty();
    }
}