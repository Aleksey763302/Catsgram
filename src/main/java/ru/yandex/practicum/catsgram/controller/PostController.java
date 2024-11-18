package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

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