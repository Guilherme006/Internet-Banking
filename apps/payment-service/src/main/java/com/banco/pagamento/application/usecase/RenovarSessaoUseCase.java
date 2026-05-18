package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.application.domain.TokenClaims;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.domain.exception.ContaNaoEncontradaException;
import com.banco.pagamento.application.domain.exception.SessaoInvalidaException;
import com.banco.pagamento.ports.inbound.RenovarSessaoPort;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.RefreshTokenRepositoryPort;
import com.banco.pagamento.ports.outbound.TokenPort;
import com.banco.pagamento.ports.outbound.UsuarioRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

public class RenovarSessaoUseCase implements RenovarSessaoPort {

    private final TokenPort tokenPort;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final ContaRepositoryPort contaRepository;
    private final SessaoFactory sessaoFactory;

    public RenovarSessaoUseCase(
            TokenPort tokenPort,
            RefreshTokenRepositoryPort refreshTokenRepository,
            UsuarioRepositoryPort usuarioRepository,
            ContaRepositoryPort contaRepository,
            SessaoFactory sessaoFactory) {
        this.tokenPort = tokenPort;
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
        this.sessaoFactory = sessaoFactory;
    }

    @Override
    @Transactional
    public AutenticacaoResultado renovar(SessaoComando comando) {
        TokenClaims claims = tokenPort.extrairRefreshToken(comando.refreshToken())
            .orElseThrow(SessaoInvalidaException::new);

        refreshTokenRepository.buscarAtivoPorJti(claims.jti())
            .orElseThrow(SessaoInvalidaException::new);
        refreshTokenRepository.revogar(claims.jti());

        Usuario usuario = usuarioRepository.buscarPorId(claims.usuarioId())
            .orElseThrow(SessaoInvalidaException::new);
        Conta conta = contaRepository.buscarPorNumero(usuario.getNumeroConta())
            .orElseThrow(() -> new ContaNaoEncontradaException(usuario.getNumeroConta()));

        return sessaoFactory.criar(usuario, conta);
    }
}
