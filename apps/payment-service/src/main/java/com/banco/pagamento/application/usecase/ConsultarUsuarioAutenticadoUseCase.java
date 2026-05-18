package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.domain.exception.UsuarioNaoEncontradoException;
import com.banco.pagamento.ports.inbound.ConsultarUsuarioAutenticadoPort;
import com.banco.pagamento.ports.outbound.UsuarioRepositoryPort;

public class ConsultarUsuarioAutenticadoUseCase implements ConsultarUsuarioAutenticadoPort {

    private final UsuarioRepositoryPort usuarioRepository;

    public ConsultarUsuarioAutenticadoUseCase(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario consultar(Long usuarioId) {
        return usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(UsuarioNaoEncontradoException::new);
    }
}
