package com.example.validacion;

import com.example.gestor.dto.ProyectoRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FechasCoherentesValidator
        implements ConstraintValidator<FechasCoherentes, ProyectoRequest> {

    @Override
    public boolean isValid(ProyectoRequest entrada, ConstraintValidatorContext contexto) {
        if (entrada == null || entrada.getFechaInicio() == null || entrada.getFechaFin() == null) {
            return true; // Si alguna no viene, la obligatoriedad se delega a @NotNull.
        }

        // Intentamos comparar de forma genérica si los objetos son Comparable.
        Object fechaFin = entrada.getFechaFin();
        Object fechaInicio = entrada.getFechaInicio();

        if (fechaFin instanceof Comparable && fechaInicio != null) {
            try {
                @SuppressWarnings("unchecked")
                Comparable<Object> cfin = (Comparable<Object>) fechaFin;
                int cmp = cfin.compareTo(fechaInicio);
                // Si fechaFin >= fechaInicio, es válida.
                if (cmp >= 0) {
                    return true;
                }
            } catch (ClassCastException ex) {
                // Tipos no comparables entre sí: delegamos la obligatoriedad o validez a otra parte.
                return true;
            }
        } else {
            // No sabemos comparar; no invalidamos aquí.
            return true;
        }

        // Si es anterior, vinculamos el mensaje de error específicamente al campo fechaFin
        contexto.disableDefaultConstraintViolation();
        contexto.buildConstraintViolationWithTemplate(contexto.getDefaultConstraintMessageTemplate())
                .addPropertyNode("fechaFin")
                .addConstraintViolation();

        return false;
    }
}