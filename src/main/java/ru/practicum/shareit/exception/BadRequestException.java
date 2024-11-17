package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.error.ErrorResponse;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class BadRequestException extends RuntimeException {
    private final ErrorResponse errorResponse;

}
