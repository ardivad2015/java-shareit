package ru.practicum.shareit.util;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.practicum.shareit.item.model.Item;

public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
