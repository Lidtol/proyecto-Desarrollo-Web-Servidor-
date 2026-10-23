package com.example.gestor.validacion;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class PrioridadValidaValidator
        implements ConstraintValidator<PrioridadValida, String> {

    private static final List<String> VALIDAS = List.of("baja", "media", "alta");

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext contexto) {
        // Un valor ausente es asunto de @NotNull, no de esta regla.
        if (valor == null) {
            return true;
        }
        return VALIDAS.contains(valor);
    }
}
