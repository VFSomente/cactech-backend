package com.cactech.api.exception;

/**
 * Excecao para cenarios de upload de arquivo invalido.
 */
public class ArquivoInvalidoException extends RuntimeException {

    public ArquivoInvalidoException(String message) {
        super(message);
    }
}