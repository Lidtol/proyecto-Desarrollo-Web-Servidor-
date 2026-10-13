package com.example.gestor.error;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ManejadorDeErrores {

    private static final Logger log =
            LoggerFactory.getLogger(ManejadorDeErrores.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail noEncontrado(
            RecursoNoEncontradoException ex,
            HttpServletRequest peticion) {

        return construir(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                peticion,
                List.of()
        );
    }

    @ExceptionHandler(ConflictoException.class)
    public ProblemDetail conflicto(
            ConflictoException ex,
            HttpServletRequest peticion) {

        return construir(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                peticion,
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest peticion) {

        List<CampoError> campos = new ArrayList<>();

        for (var error : ex.getBindingResult().getFieldErrors()) {
            campos.add(new CampoError(
                    error.getField(),
                    error.getDefaultMessage()
            ));
        }

        return construir(
                HttpStatus.BAD_REQUEST,
                "Hay campos que no son válidos",
                peticion,
                campos
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail cuerpoIlegible(
            HttpMessageNotReadableException ex,
            HttpServletRequest peticion) {

        return construir(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es un JSON válido",
                peticion,
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail inesperado(
            Exception ex,
            HttpServletRequest peticion) {

        log.error(
                "Error no controlado en {}",
                peticion.getRequestURI(),
                ex
        );

        return construir(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ha ocurrido un error inesperado",
                peticion,
                List.of()
        );
    }

    private ProblemDetail construir(
            HttpStatus estado,
            String mensaje,
            HttpServletRequest peticion,
            List<CampoError> campos) {

        ProblemDetail problema =
                ProblemDetail.forStatusAndDetail(estado, mensaje);

        problema.setTitle(estado.getReasonPhrase());
        problema.setInstance(URI.create(peticion.getRequestURI()));
        problema.setProperty("momento", LocalDateTime.now());

        if (!campos.isEmpty()) {
            problema.setProperty("invalidParams", campos);
        }

        return problema;
    }
}
