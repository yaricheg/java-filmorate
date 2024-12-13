package ru.yandex.practicum.filmorate.validator.containSpaces;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ContainSpacesValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContainSpaces {
    String message() default "Поле не должно содержать пробелы";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

}
