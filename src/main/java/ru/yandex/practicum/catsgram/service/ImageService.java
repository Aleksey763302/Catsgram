package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.model.Post;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final PostService postService;
    private final ImageRepository repository;

    @Value(value = "${app.imageDirectory}")
    private String imageDirectory;

    public ImageData getImageData(long imageId) {
        Optional<Image> imageOptional = repository.getImage(imageId);
        if (imageOptional.isEmpty()) {
            throw new NotFoundException(String.format("изображение с id %d не найдено", imageId));
        }
        Image image = imageOptional.get();
        byte[] data = loadFile(image);
        return new ImageData(data, image.getOriginalFileName());
    }

    private byte[] loadFile(Image image) {
        Path path = Paths.get(image.getFilePath());
        if (Files.exists(path)) {
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new ImageFileException(String.format("Ошибка чтения файла.  Id: %d, name: %s",
                        image.getId(), image.getOriginalFileName()));
            }
        } else {
            throw new ImageFileException(String.format("Файл не найден. Id: %d, name: %s",
                    image.getId(), image.getOriginalFileName()));
        }
    }

    public List<Image> getPostImage(final long postId) {
        return repository.getImagesPost(postId);
    }

    public Path saveFile(MultipartFile file, Post post) {
        try {
            String uniqueFileName = String.format("%d.%s", Instant.now().toEpochMilli(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename()));
            Path uploadPath = Paths.get(imageDirectory, String.valueOf(post.getAuthorId()), post.getId().toString());
            Path filePath = uploadPath.resolve(uniqueFileName);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            file.transferTo(filePath);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public List<Image> saveImages(long postId, List<MultipartFile> files) {
        return files.stream()
                .map(file -> saveImage(postId, file))
                .toList();
    }

    private Image saveImage(long postId, MultipartFile file) {
        Optional<Post> postOptional = postService.getPostById(postId);
        Post post;
        if (postOptional.isEmpty()) {
            throw new NotFoundException(String.format("Пост с ID: %d не обнаружен", postId));
        }
        post = postOptional.get();
        Path filePath = saveFile(file, post);
        Image image = new Image();
        image.setFilePath(filePath.toString());
        image.setPostId(postId);
        image.setOriginalFileName(file.getOriginalFilename());
        long imageId = repository.addImage(image);
        image.setId(imageId);
        return image;
    }
}
