package com.banco.pagamento.application.domain.exception;

public class BoletoNaoEncontradoException extends RuntimeException {

    public BoletoNaoEncontradoException(String codigoBarra) {
        super("Boleto não encontrado: " + codigoBarra);
    }
}
