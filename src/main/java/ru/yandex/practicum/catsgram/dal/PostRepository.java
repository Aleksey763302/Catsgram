package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.PostRowMapper;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository extends BaseRepository<Post> {
    public PostRepository(NamedParameterJdbcTemplate jdbc, PostRowMapper mapper) {
        super(jdbc, mapper);
    }

    static final String CREATE_USER = "INSERT INTO posts (author_id,description,post_date) " +
            "VALUES(:authorID, :description, :postDate)";
    static final String GET_POST_BY_ID = "SELECT * FROM posts WHERE id = :postID";
    static final String GET_ALL_POSTS = "SELECT * FROM posts";
    static final String UPDATE_POST = "UPDATE posts SET description = :description WHERE id = :postID";

    public Optional<Post> createPost(Post post) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("authorID", post.getAuthorId())
                .addValue("description", post.getDescription())
                .addValue("postDate", LocalDateTime.ofInstant(post.getPostDate(), ZoneId.systemDefault()));
        return getPostById(insert(CREATE_USER, params));
    }

    public Optional<Post> updatePost(Post post) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("description",post.getDescription())
                .addValue("postID", post.getId());
        if (update(UPDATE_POST, params)) {
            return getPostById(post.getId());
        }
        return Optional.empty();
    }

    public Optional<Post> getPostById(Long postId) {
        SqlParameterSource params = new MapSqlParameterSource().addValue("postID", postId);
        return findOne(GET_POST_BY_ID, params);
    }

    public List<Post> getListPosts() {
        SqlParameterSource params = new MapSqlParameterSource();
        return findMany(GET_ALL_POSTS, params);
    }
}
