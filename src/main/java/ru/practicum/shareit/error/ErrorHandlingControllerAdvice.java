package ru.practicum.shareit.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<String> errorList = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Ошибка валидации поля %s: %s. некорректное значение %s",
                        error.getField(), error.getDefaultMessage(),
                        Objects.isNull(error.getRejectedValue()) ? "" : error.getRejectedValue().toString()))
                .collect(Collectors.toList());
        log.error("onMethodArgumentNotValidException. {}", errorList);
        return new ErrorResponse(errorList);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onConstraintViolationException(ConstraintViolationException e) {
        final List<String> errorList = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        log.error("onConstraintViolationException. {}", errorList);
        return new ErrorResponse(errorList);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse onConditionsNotMetException(ConditionsNotMetException e) {
        return e.getErrorResponse();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onNotFoundException(NotFoundException e) {
        List<String> errorList = new ArrayList<>();
        errorList.add(e.getMessage());
        return  new ErrorResponse(errorList);
    }
}