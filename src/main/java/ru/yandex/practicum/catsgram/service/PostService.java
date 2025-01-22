package ru.yandex.practicum.catsgram.service;

import ch.qos.logback.classic.Level;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final UserService userService;
    private final PostRepository repository;

    public Optional<Post> getPostById(final Long id) {
        return repository.getPostById(id);
    }

    public List<Post> findAll(final String sortOrder, final String page, final String size) {
        int startPage = Integer.parseInt(page);
        int countPosts = Integer.parseInt(size);
        Comparator<Post> comparator = getComparator(sortOrder);
        TreeSet<Post> sortedPosts = new TreeSet<>(comparator);
        sortedPosts.addAll(repository.getListPosts());
        List<Post> postsList = new ArrayList<>(sortedPosts);
        sortedPosts.clear();
        return postsList.stream().skip(startPage).limit(countPosts).toList();
    }

    public Optional<Post> create(Post post) {
        if (userService.findUserById(post.getAuthorId()).isEmpty()) {
            log.debug("«Автор с id = {} не найден» ", post.getAuthorId());
            throw new ConditionsNotMetException(String.format("«Автор с id = %d не найден»", post.getAuthorId()));
        }
        if (Objects.isNull(post.getDescription()) || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        post.setPostDate(Instant.now());
        return repository.createPost(post);
    }

    public Optional<Post> update(Post newPost) {
        if (Objects.isNull(newPost.getId())) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (repository.getPostById(newPost.getId()).isPresent()) {

            if (Objects.isNull(newPost.getDescription()) || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            return repository.updatePost(newPost);

        }else {
            throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
        }

    }

    private Comparator<Post> getComparator(String sortOrder) {
        SortOrder sort = SortOrder.from(sortOrder);
        if (sort == null) {
            sort = SortOrder.ASCENDING;
        }
        return switch (sort) {
            case ASCENDING -> (Post post1, Post post2) -> {
                if (post1.getPostDate().isAfter(post2.getPostDate())) {
                    return 1;
                } else if (post1.getPostDate().equals(post2.getPostDate())) {
                    return 0;
                } else {
                    return -1;
                }
            };
            case DESCENDING -> (Post post1, Post post2) -> {
                if (post1.getPostDate().isBefore(post2.getPostDate())) {
                    return 1;
                } else if (post1.getPostDate().equals(post2.getPostDate())) {
                    return 0;
                } else {
                    return -1;
                }
            };
        };
    }
}
