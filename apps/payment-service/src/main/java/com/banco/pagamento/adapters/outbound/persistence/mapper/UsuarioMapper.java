package com.banco.pagamento.adapters.outbound.persistence.mapper;

import com.banco.pagamento.adapters.outbound.persistence.entity.UsuarioEntity;
import com.banco.pagamento.application.domain.Endereco;
import com.banco.pagamento.application.domain.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }
        return Usuario.builder()
            .id(entity.getId())
            .nome(entity.getNome())
            .email(entity.getEmail())
            .cpf(entity.getCpf())
            .senhaHash(entity.getSenhaHash())
            .numeroConta(entity.getNumeroConta())
            .endereco(Endereco.builder()
                .cep(entity.getCep())
                .logradouro(entity.getLogradouro())
                .numero(entity.getNumero())
                .complemento(entity.getComplemento())
                .bairro(entity.getBairro())
                .cidade(entity.getCidade())
                .uf(entity.getUf())
                .build())
            .build();
    }

    public UsuarioEntity toEntity(Usuario usuario) {
        return UsuarioEntity.builder()
            .id(usuario.getId())
            .nome(usuario.getNome())
            .email(usuario.getEmail())
            .cpf(usuario.getCpf())
            .senhaHash(usuario.getSenhaHash())
            .numeroConta(usuario.getNumeroConta())
            .cep(usuario.getEndereco().getCep())
            .logradouro(usuario.getEndereco().getLogradouro())
            .numero(usuario.getEndereco().getNumero())
            .complemento(usuario.getEndereco().getComplemento())
            .bairro(usuario.getEndereco().getBairro())
            .cidade(usuario.getEndereco().getCidade())
            .uf(usuario.getEndereco().getUf())
            .build();
    }
}
