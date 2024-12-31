package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;
import ru.yandex.practicum.catsgram.service.SortOrder;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public Optional<Post> getPostById(@PathVariable final Long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/")
    public Collection<Post> findAll(@RequestParam(defaultValue = "asc") String sort,
                                    @RequestParam(defaultValue = "0") String from,
                                    @RequestParam(defaultValue = "10") String size) {
        if (SortOrder.from(sort) == null) {
            throw new ParameterNotValidException(sort, "некорректный параметр sort");
        }
        if (!size.isEmpty()) {
            try {
                if (Integer.parseInt(size) < 0) {
                    throw new ParameterNotValidException(size, "\"Некорректный размер выборки. size должен быть больше нуля\"");
                }
            } catch (NumberFormatException e) {
                throw new ParameterNotValidException(from, "некорректный ввод параметра size");
            }
        }
        if (!from.isEmpty()) {
            try {
                if (Integer.parseInt(from) < 0) {
                    throw new ParameterNotValidException(from, "Некорректный размер выборки. from должен быть больше нуля");
                }
            } catch (NumberFormatException e) {
                throw new ParameterNotValidException(from, "некорректный ввод параметра from");
            }
        }
        return postService.findAll(sort, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}