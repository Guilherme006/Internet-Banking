package com.banco.pagamento.ports.outbound;

import com.banco.pagamento.application.domain.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    boolean existePorEmail(String email);

    boolean existePorCpf(String cpf);

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorId(Long id);

    Usuario salvar(Usuario usuario);
}
