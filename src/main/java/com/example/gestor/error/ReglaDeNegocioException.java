package com.example.gestor.error;

/** Una regla del dominio que depende de los datos existentes se ha incumplido. No sabe nada de HTTP. */
public class ReglaDeNegocioException extends RuntimeException {

    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}
