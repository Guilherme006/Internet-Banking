package com.banco.pagamento.ports.outbound;

import com.banco.pagamento.application.domain.Usuario;

public interface TokenPort {

    String gerar(Usuario usuario);
}
