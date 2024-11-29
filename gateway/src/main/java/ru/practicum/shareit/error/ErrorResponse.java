package ru.practicum.shareit.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ErrorResponse {
    private final List<String> errorList;
}