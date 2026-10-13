package com.example.validacion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PrioridadValidaValidator.class)
public @interface PrioridadValida {

    String message() default
        "La prioridad debe ser baja, media o alta";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}