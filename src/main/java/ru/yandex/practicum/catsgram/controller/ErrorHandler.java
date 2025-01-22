package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;

import java.sql.SQLException;

@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler
    public ErrorResponse handleNotFound(final NotFoundException e) {
        return ErrorResponse.create(e,HttpStatus.NOT_FOUND,e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse handleDuplicate(final DuplicatedDataException e) {
        return ErrorResponse.create(e,HttpStatus.CONFLICT,e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse handleCondition(final ConditionsNotMetException e) {
        return ErrorResponse.create(e,HttpStatus.UNPROCESSABLE_ENTITY,e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse handleIncorrectValue(final ParameterNotValidException e) {
        return ErrorResponse.create(e,HttpStatus.BAD_REQUEST,String.format("Некорректное значение параметра <%s>: <%s>", e.getParameter(), e.getReason()));
    }

    @ExceptionHandler
    public ErrorResponse handleSQLError(final SQLException e) {
        return ErrorResponse.create(e,HttpStatus.BAD_REQUEST,"ошибка при обращении к БД");
    }

    @ExceptionHandler
    public ErrorResponse handleInternalServerError(final Throwable e) {
        return ErrorResponse.create(e,HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
    }
}
