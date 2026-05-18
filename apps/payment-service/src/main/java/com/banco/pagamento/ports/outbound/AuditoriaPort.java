package com.banco.pagamento.ports.outbound;

import com.banco.pagamento.application.domain.AuditoriaEvento;

public interface AuditoriaPort {

    void registrar(AuditoriaEvento evento);
}
