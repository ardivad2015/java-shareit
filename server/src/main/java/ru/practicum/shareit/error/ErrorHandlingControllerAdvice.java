package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.InsufficientPermissionException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse onConditionsNotMetException(ConditionsNotMetException e) {
        return e.getErrorResponse();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onBadRequestException(BadRequestException e) {
        return  e.getErrorResponse();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onNotFoundException(NotFoundException e) {
        List<String> errorList = new ArrayList<>();
        errorList.add(e.getMessage());
        return  new ErrorResponse(errorList);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onInsufficientPermissionException(InsufficientPermissionException e) {
        List<String> errorList = new ArrayList<>();
        errorList.add(e.getMessage());
        return  new ErrorResponse(errorList);
    }
}