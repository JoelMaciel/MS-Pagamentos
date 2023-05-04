package com.food.pagamentos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PagamentoNaoExisteException extends RuntimeException{

    public PagamentoNaoExisteException() {
    }

    public PagamentoNaoExisteException(String message) {
        super(message);
    }
}
