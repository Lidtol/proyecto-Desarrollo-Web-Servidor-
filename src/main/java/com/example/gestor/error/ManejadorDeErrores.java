package com.example.gestor.error;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Único sitio donde se construye una respuesta de error.
 *
 * Extiende ResponseEntityExceptionHandler para cubrir también las excepciones que lanza
 * Spring antes de llegar a un controlador (ruta inexistente, método no permitido,
 * Content-Type incorrecto, parámetro con tipo erróneo...). Todas pasan por
 * handleExceptionInternal, que les da el mismo formato ProblemDetail.
 */
@RestControllerAdvice
public class ManejadorDeErrores extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ManejadorDeErrores.class);

    private static final String DETALLE_INESPERADO = "Ha ocurrido un error inesperado";

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Object> noEncontrado(RecursoNoEncontradoException ex, WebRequest peticion) {
        return responder(ex, HttpStatus.NOT_FOUND, ex.getMessage(), peticion);
    }

    @ExceptionHandler(ReglaDeNegocioException.class)
    public ResponseEntity<Object> reglaIncumplida(ReglaDeNegocioException ex, WebRequest peticion) {
        return responder(ex, HttpStatus.CONFLICT, ex.getMessage(), peticion);
    }

    /** Lo que ninguna otra regla ha recogido: se registra dentro y no se cuenta nada fuera. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> inesperado(Exception ex, WebRequest peticion) {
        log.error("Error no controlado en {}", ruta(peticion), ex);
        return responder(ex, HttpStatus.INTERNAL_SERVER_ERROR, DETALLE_INESPERADO, peticion);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders cabeceras,
            HttpStatusCode estado,
            WebRequest peticion) {

        List<CampoError> campos = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            campos.add(new CampoError(error.getField(), error.getDefaultMessage()));
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            campos.add(new CampoError(error.getObjectName(), error.getDefaultMessage()));
        }

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Hay campos que no son válidos");
        problema.setProperty("invalidParams", campos);

        return handleExceptionInternal(ex, problema, cabeceras, estado, peticion);
    }

    /**
     * Punto único por el que pasa TODA respuesta de error, sea nuestra o de Spring:
     * misma forma (type/title/status/detail/instance + momento) y detalle en español.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object cuerpo,
            HttpHeaders cabeceras,
            HttpStatusCode estado,
            WebRequest peticion) {

        ResponseEntity<Object> respuesta =
                super.handleExceptionInternal(ex, cuerpo, cabeceras, estado, peticion);

        if (respuesta.getBody() instanceof ProblemDetail problema) {
            normalizar(problema, ex, peticion);
        }
        return respuesta;
    }

    private ResponseEntity<Object> responder(
            Exception ex, HttpStatus estado, String detalle, WebRequest peticion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, detalle);
        return handleExceptionInternal(ex, problema, new HttpHeaders(), estado, peticion);
    }

    private void normalizar(ProblemDetail problema, Exception ex, WebRequest peticion) {
        HttpStatus estado = HttpStatus.resolve(problema.getStatus());
        if (estado != null) {
            problema.setTitle(estado.getReasonPhrase());
        }
        problema.setInstance(URI.create(ruta(peticion)));
        problema.setProperty("momento", OffsetDateTime.now().toString());

        // Las excepciones nuestras ya traen su detalle en español.
        if (ex instanceof RecursoNoEncontradoException
                || ex instanceof ReglaDeNegocioException
                || ex instanceof MethodArgumentNotValidException) {
            return;
        }

        if (ex instanceof HttpMessageNotReadableException) {
            problema.setDetail("El cuerpo de la petición no es un JSON válido o le faltan datos");
        } else if (ex instanceof MissingServletRequestParameterException falta) {
            problema.setDetail("Falta el parámetro obligatorio '" + falta.getParameterName() + "'");
        } else if (ex instanceof TypeMismatchException tipo) {
            problema.setDetail("El valor '" + tipo.getValue() + "' no es válido para '"
                    + tipo.getPropertyName() + "'");
        } else if (problema.getStatus() == HttpStatus.NOT_FOUND.value()) {
            problema.setDetail("La ruta solicitada no existe");
        } else if (problema.getStatus() == HttpStatus.METHOD_NOT_ALLOWED.value()) {
            problema.setDetail("Este método no está permitido en esta ruta");
        } else if (problema.getStatus() == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
            problema.setDetail("Tipo de contenido no soportado: envía el cuerpo como application/json");
        } else if (problema.getStatus() == HttpStatus.NOT_ACCEPTABLE.value()) {
            problema.setDetail("No se puede responder en el formato que pides");
        } else if (problema.getStatus() >= 500) {
            problema.setDetail(DETALLE_INESPERADO);
        }
    }

    private String ruta(WebRequest peticion) {
        if (peticion instanceof ServletWebRequest servlet) {
            HttpServletRequest http = servlet.getRequest();
            return http.getRequestURI();
        }
        return "/";
    }
}
