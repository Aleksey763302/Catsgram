package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.ImageRowMapper;
import ru.yandex.practicum.catsgram.model.Image;

import java.util.List;
import java.util.Optional;

@Repository
public class ImageRepository extends BaseRepository<Image> {
    public ImageRepository(NamedParameterJdbcTemplate jdbc, ImageRowMapper mapper){
        super(jdbc,mapper);
    }
    private static final String ADD_IMAGE = "INSERT INTO image_storage (original_name,file_path,post_id)" +
            "VALUES (:original_name, :file_path, :post_id)";
    private static final String FOUND_IMAGES_POST = "SELECT * FROM image_storage WHERE post_id = :post_id";
    private static final String FOUND_IMAGE_BY_ID = "SELECT * FROM image_storage WHERE id = :id";

    public Long addImage(Image image){
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("original_name",image.getOriginalFileName())
                .addValue("file_path",image.getFilePath())
                .addValue("post_id",image.getPostId());
        return insert(ADD_IMAGE,params);
    }

    public List<Image> getImagesPost(final Long postId){
        SqlParameterSource params = new MapSqlParameterSource().addValue("post_id",postId);
        return findMany(FOUND_IMAGES_POST,params);
    }
    public Optional<Image> getImage(final Long imageId){
        SqlParameterSource params = new MapSqlParameterSource().addValue("id",imageId);
        return findOne(FOUND_IMAGE_BY_ID,params);
    }
}
