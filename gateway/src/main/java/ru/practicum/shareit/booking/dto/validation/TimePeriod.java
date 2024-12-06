package ru.practicum.shareit.booking.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {TimePeriodValidator.class})
public @interface TimePeriod {
    String message() default "Дата начала должна быть больше даты окончания";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}