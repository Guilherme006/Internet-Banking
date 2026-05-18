package com.banco.pagamento.ports.outbound;

import com.banco.pagamento.application.domain.Conta;

import java.util.Optional;

public interface ContaRepositoryPort {

    Optional<Conta> buscarComLockPessimista(String numeroConta);

    Optional<Conta> buscarPorNumero(String numeroConta);

    Conta salvar(Conta conta);
}
