package com.banco.pagamento.application.domain;

import com.banco.pagamento.application.domain.exception.BoletoJaPagoException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class Boleto {

    private final Long id;
    private final String codigoBarra;
    private final String beneficiario;
    private final String pagador;
    private final BigDecimal valor;
    private final LocalDate dataVencimento;
    private StatusBoleto status;

    public void liquidar() {
        if (this.status != StatusBoleto.PENDENTE) {
            throw new BoletoJaPagoException(
                String.format("Boleto já se encontra com status '%s'. Código: %s",
                    this.status, this.codigoBarra)
            );
        }
        this.status = StatusBoleto.PAGO;
    }
}
