package com.banco.pagamento.ports.outbound;

import com.banco.pagamento.application.usecase.PagamentoEvento;

public interface NotificacaoKafkaPort {

        void publicar(PagamentoEvento evento);
}
