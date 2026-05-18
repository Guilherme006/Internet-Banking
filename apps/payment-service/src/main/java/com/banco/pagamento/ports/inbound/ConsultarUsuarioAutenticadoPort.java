package com.banco.pagamento.ports.inbound;

import com.banco.pagamento.application.domain.Usuario;

public interface ConsultarUsuarioAutenticadoPort {

    Usuario consultar(Long usuarioId);
}
