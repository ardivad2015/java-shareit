package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.error.ErrorResponse;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ConditionsNotMetException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public static ConditionsNotMetException simpleConditionsNotMetException(String message) {
        final List<String> errorList = new ArrayList<>();
        errorList.add(message);
        return new ConditionsNotMetException(new ErrorResponse(errorList));
    }
}