package ru.yandex.practicum.catsgram.service;

import ch.qos.logback.classic.Level;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;

@Service
public class PostService {
    private final UserService userService;
    private final Map<Long, Post> posts = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Optional<Post> getPostById(final Long id) {
        if (posts.containsKey(id)) {
            return Optional.of(posts.get(id));
        } else {
            return Optional.empty();
        }
    }

    public Collection<Post> findAll(final String sortOrder, final String page, final String size) {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
                .setLevel(Level.DEBUG);
        int startPage = Integer.parseInt(page);
        int countPosts = Integer.parseInt(size);
        log.debug("параметры: sortOrder {} page {} size {}", sortOrder, page, size);
        Comparator<Post> comparator = getComparator(sortOrder);
        TreeSet<Post> sortedPosts = new TreeSet<>(comparator);
        sortedPosts.addAll(posts.values());
        List<Post> postsList = new ArrayList<>(sortedPosts);
        sortedPosts.clear();
        return postsList.stream().skip(startPage).limit(countPosts).toList();
    }

    public Post create(Post post) {
        if (userService.findUserById(post.getAuthorId()) == null) {
            log.debug("«Автор с id = {} не найден» ", post.getAuthorId());
            throw new ConditionsNotMetException(String.format("«Автор с id = %d не найден»", post.getAuthorId()));
        }
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
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

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
