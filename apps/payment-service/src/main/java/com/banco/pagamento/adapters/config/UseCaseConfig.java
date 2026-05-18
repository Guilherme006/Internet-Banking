package com.banco.pagamento.adapters.config;

import com.banco.pagamento.application.usecase.AutenticarUsuarioUseCase;
import com.banco.pagamento.application.usecase.CadastrarUsuarioUseCase;
import com.banco.pagamento.application.usecase.ConsultarUsuarioAutenticadoUseCase;
import com.banco.pagamento.application.usecase.ConsultarBoletoUseCase;
import com.banco.pagamento.application.usecase.ConsultarExtratoUseCase;
import com.banco.pagamento.application.usecase.EncerrarSessaoUseCase;
import com.banco.pagamento.application.usecase.PagamentoComando;
import com.banco.pagamento.application.usecase.PagamentoResultado;
import com.banco.pagamento.application.usecase.ProcessarPagamentoUseCase;
import com.banco.pagamento.application.usecase.RenovarSessaoUseCase;
import com.banco.pagamento.application.usecase.SessaoFactory;
import com.banco.pagamento.ports.inbound.AutenticarUsuarioPort;
import com.banco.pagamento.ports.inbound.CadastrarUsuarioPort;
import com.banco.pagamento.ports.inbound.ConsultarBoletoPort;
import com.banco.pagamento.ports.inbound.ConsultarExtratoPort;
import com.banco.pagamento.ports.inbound.ConsultarUsuarioAutenticadoPort;
import com.banco.pagamento.ports.inbound.EncerrarSessaoPort;
import com.banco.pagamento.ports.inbound.ProcessarPagamentoPort;
import com.banco.pagamento.ports.inbound.RenovarSessaoPort;
import com.banco.pagamento.ports.outbound.BancoCentralPort;
import com.banco.pagamento.ports.outbound.BoletoRepositoryPort;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.IdempotenciaPort;
import com.banco.pagamento.ports.outbound.NotificacaoKafkaPort;
import com.banco.pagamento.ports.outbound.PasswordHasherPort;
import com.banco.pagamento.ports.outbound.RefreshTokenRepositoryPort;
import com.banco.pagamento.ports.outbound.TokenPort;
import com.banco.pagamento.ports.outbound.TransacaoRepositoryPort;
import com.banco.pagamento.ports.outbound.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class UseCaseConfig {

    @Bean
    public ProcessarPagamentoUseCase processarPagamentoUseCase(
            ContaRepositoryPort contaRepositoryPort,
            BoletoRepositoryPort boletoRepositoryPort,
            BancoCentralPort bancoCentralPort,
            NotificacaoKafkaPort notificacaoKafkaPort,
            IdempotenciaPort idempotenciaPort,
            TransacaoRepositoryPort transacaoRepositoryPort) {
        return new ProcessarPagamentoUseCase(
            contaRepositoryPort,
            boletoRepositoryPort,
            bancoCentralPort,
            notificacaoKafkaPort,
            idempotenciaPort,
            transacaoRepositoryPort
        );
    }

    @Bean
    public ConsultarBoletoPort consultarBoletoPort(BoletoRepositoryPort boletoRepositoryPort) {
        return new ConsultarBoletoUseCase(boletoRepositoryPort);
    }

    @Bean
    public ConsultarExtratoPort consultarExtratoPort(TransacaoRepositoryPort transacaoRepositoryPort) {
        return new ConsultarExtratoUseCase(transacaoRepositoryPort);
    }

    @Bean
    public SessaoFactory sessaoFactory(
            TokenPort tokenPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            @Value("${security.jwt.expiration-minutes}") long accessExpirationMinutes,
            @Value("${security.jwt.refresh-expiration-days}") long refreshExpirationDays) {
        return new SessaoFactory(
            tokenPort,
            refreshTokenRepositoryPort,
            accessExpirationMinutes * 60,
            refreshExpirationDays * 24 * 60 * 60
        );
    }

    @Bean
    public AutenticarUsuarioPort autenticarUsuarioPort(
            UsuarioRepositoryPort usuarioRepositoryPort,
            ContaRepositoryPort contaRepositoryPort,
            PasswordHasherPort passwordHasherPort,
            SessaoFactory sessaoFactory) {
        return new AutenticarUsuarioUseCase(
            usuarioRepositoryPort,
            contaRepositoryPort,
            passwordHasherPort,
            sessaoFactory
        );
    }

    @Bean
    public CadastrarUsuarioPort cadastrarUsuarioPort(
            UsuarioRepositoryPort usuarioRepositoryPort,
            ContaRepositoryPort contaRepositoryPort,
            PasswordHasherPort passwordHasherPort,
            SessaoFactory sessaoFactory) {
        return new CadastrarUsuarioUseCase(
            usuarioRepositoryPort,
            contaRepositoryPort,
            passwordHasherPort,
            sessaoFactory
        );
    }

    @Bean
    public RenovarSessaoPort renovarSessaoPort(
            TokenPort tokenPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort,
            ContaRepositoryPort contaRepositoryPort,
            SessaoFactory sessaoFactory) {
        return new RenovarSessaoUseCase(
            tokenPort,
            refreshTokenRepositoryPort,
            usuarioRepositoryPort,
            contaRepositoryPort,
            sessaoFactory
        );
    }

    @Bean
    public EncerrarSessaoPort encerrarSessaoPort(
            TokenPort tokenPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        return new EncerrarSessaoUseCase(tokenPort, refreshTokenRepositoryPort);
    }

    @Bean
    public ConsultarUsuarioAutenticadoPort consultarUsuarioAutenticadoPort(
            UsuarioRepositoryPort usuarioRepositoryPort) {
        return new ConsultarUsuarioAutenticadoUseCase(usuarioRepositoryPort);
    }

    @Bean
    public ProcessarPagamentoPort processarPagamentoPort(
            ProcessarPagamentoUseCase processarPagamentoUseCase,
            TransactionTemplate transactionTemplate) {
        return (PagamentoComando comando) -> {
            PagamentoResultado resultado = transactionTemplate.execute(
                status -> processarPagamentoUseCase.processar(comando)
            );
            if (resultado == null) {
                throw new IllegalStateException("Use case retornou resultado nulo inesperadamente");
            }
            return resultado;
        };
    }
}
