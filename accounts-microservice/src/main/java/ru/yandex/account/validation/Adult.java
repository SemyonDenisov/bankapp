package ru.yandex.account.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AdultValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Adult {
    String message() default "Пользователь должен быть старше 18 лет";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

