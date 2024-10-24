package ru.practicum.shareit.exception;

public class NotFoundBadRequestException extends NotFoundException {
    public NotFoundBadRequestException(String message) {
        super(message);
    }
}
