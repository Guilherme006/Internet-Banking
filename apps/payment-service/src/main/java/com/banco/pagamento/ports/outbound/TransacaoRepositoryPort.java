package com.banco.pagamento.ports.outbound;

import com.banco.pagamento.application.domain.Transacao;
import com.banco.pagamento.application.usecase.ConsultarExtratoQuery;
import com.banco.pagamento.application.usecase.ExtratoResultado;

public interface TransacaoRepositoryPort {

    Transacao salvar(Transacao transacao);

    ExtratoResultado buscarExtrato(ConsultarExtratoQuery query);
}
