package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.service.ImageService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping(value = "/images/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadImage(@PathVariable long imageId) {
        ImageData imageData = imageService.getImageData(imageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(imageData.getName())
                        .build());
        return new ResponseEntity<>(imageData.getData(), headers, HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}/images")
    public List<Image> getPostImage(@PathVariable final long postId) {
        return imageService.getPostImage(postId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts/{postId}/images")
    public List<Image> createImage(@PathVariable final long postId,
                                   @RequestParam("image") List<MultipartFile> files) {

        return imageService.saveImages(postId, files);
    }
}
