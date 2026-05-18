package com.banco.pagamento.application.domain;

import com.banco.pagamento.application.domain.exception.SaldoInsuficienteException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class Conta {

    private final Long id;
    private final String numeroConta;
    private final String agencia;
    private final String titular;
    private BigDecimal saldo;
    private final Long versao;

    public void validarSaldo(BigDecimal valor) {
        if (this.saldo.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException(
                String.format("Saldo insuficiente. Disponível: R$ %.2f | Solicitado: R$ %.2f",
                    this.saldo, valor)
            );
        }
    }

    public void debitar(BigDecimal valor) {
        validarSaldo(valor);
        this.saldo = this.saldo.subtract(valor);
    }
}
