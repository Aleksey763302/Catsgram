package ru.yandex.practicum.catsgram.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

@Component
public class PostRowMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong(1));
        post.setAuthorId(rs.getLong(2));
        post.setDescription(rs.getString(3));
        Timestamp registrationDate = rs.getTimestamp("post_date");
        post.setPostDate(Instant.from(registrationDate.toInstant()));
        return post;
    }
}
