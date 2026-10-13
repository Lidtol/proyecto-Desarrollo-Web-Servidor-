package com.example.gestor.validacion;

import com.example.gestor.dto.ProyectoRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FechasCoherentesValidator implements ConstraintValidator<FechasCoherentes, ProyectoRequest> {

    @Override
    public boolean isValid(ProyectoRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate inicio = value.fechaInicio();
        LocalDate fin = value.fechaFin();

        if (fin == null) {
            return true;
        }

        if (inicio == null) {
            return true;
        }

        if (fin.isBefore(inicio)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("fechaFin")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
