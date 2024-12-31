package ru.yandex.practicum.catsgram.exception;

public class EmptyResultDataAccessException extends RuntimeException {
    public EmptyResultDataAccessException(String message) {
        super(message);
    }
}
