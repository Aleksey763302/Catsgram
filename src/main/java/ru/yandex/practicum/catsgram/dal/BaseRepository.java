package ru.yandex.practicum.catsgram.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.catsgram.exception.EmptyResultDataAccessException;
import ru.yandex.practicum.catsgram.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final NamedParameterJdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, SqlParameterSource params) {
        try {
            T result = jdbc.queryForObject(query, params, mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, SqlParameterSource params) {
        return jdbc.query(query, params, mapper);
    }

    protected boolean delete(String query, SqlParameterSource params) {
        int rowsDeleted = jdbc.update(query, params);
        return rowsDeleted > 0;
    }

    protected boolean update(String query, SqlParameterSource params) throws DataAccessException {
        int rowsUpdated;
        try {
            rowsUpdated = jdbc.update(query, params);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        return true;
    }
    protected Long insert(String query, SqlParameterSource params){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsUpdated;
        try {
            rowsUpdated = jdbc.update(query, params, keyHolder, new String[]{"id"});
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось добавить данные");
        }
        return (Long) keyHolder.getKey();
    }
}
