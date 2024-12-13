package ru.yandex.practicum.filmorate.validator.containSpaces;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContainSpacesValidator implements ConstraintValidator<ContainSpaces, String> {

    @Override
    public void initialize(ContainSpaces constraintAnnotation) {
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        return login != null && !login.contains(" ") && !login.isBlank();
    }
}
