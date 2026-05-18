package com.banco.pagamento.adapters.outbound.persistence.adapter;

import com.banco.pagamento.adapters.outbound.persistence.entity.IdempotenciaEntity;
import com.banco.pagamento.adapters.outbound.persistence.repository.IdempotenciaJpaRepository;
import com.banco.pagamento.application.usecase.PagamentoResultado;
import com.banco.pagamento.ports.outbound.IdempotenciaPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotenciaAdapter implements IdempotenciaPort {

    private static final long TTL_HORAS = 24;

    private final IdempotenciaJpaRepository idempotenciaJpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<PagamentoResultado> buscar(String chave) {
        return idempotenciaJpaRepository
            .findByChaveAndExpiraEmAfter(chave, LocalDateTime.now())
            .map(entity -> {
                try {
                    return objectMapper.readValue(entity.getResposta(), PagamentoResultado.class);
                } catch (JsonProcessingException e) {
                    log.error("Erro ao desserializar resultado de idempotência. Chave: {}", chave, e);
                    return null;
                }
            });
    }

    @Override
    public void salvar(String chave, PagamentoResultado resultado) {
        try {
            String respostaJson = objectMapper.writeValueAsString(resultado);
            IdempotenciaEntity entity = IdempotenciaEntity.builder()
                .chave(chave)
                .resposta(respostaJson)
                .criadoEm(LocalDateTime.now())
                .expiraEm(LocalDateTime.now().plusHours(TTL_HORAS))
                .build();
            idempotenciaJpaRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Chave de idempotência já registrada por requisição concorrente: {}", chave);
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar resultado para idempotência. Chave: {}", chave, e);
            throw new RuntimeException("Erro ao persistir chave de idempotência", e);
        }
    }
}
