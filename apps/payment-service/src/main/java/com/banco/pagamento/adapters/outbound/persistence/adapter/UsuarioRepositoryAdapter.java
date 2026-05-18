package com.banco.pagamento.adapters.outbound.persistence.adapter;

import com.banco.pagamento.adapters.outbound.persistence.mapper.UsuarioMapper;
import com.banco.pagamento.adapters.outbound.persistence.repository.UsuarioJpaRepository;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.ports.outbound.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public boolean existePorEmail(String email) {
        return usuarioJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existePorCpf(String cpf) {
        return usuarioJpaRepository.existsByCpf(cpf);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioJpaRepository.findByEmail(email).map(usuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioJpaRepository.findById(id).map(usuarioMapper::toDomain);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        return usuarioMapper.toDomain(usuarioJpaRepository.save(usuarioMapper.toEntity(usuario)));
    }
}
