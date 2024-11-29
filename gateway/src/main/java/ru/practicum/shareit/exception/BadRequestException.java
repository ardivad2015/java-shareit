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

    public static BadRequestException simpleBadRequestException(String message) {
        final List<String> errorList = new ArrayList<>();
        errorList.add(message);
        return new BadRequestException(new ErrorResponse(errorList));
    }

}
