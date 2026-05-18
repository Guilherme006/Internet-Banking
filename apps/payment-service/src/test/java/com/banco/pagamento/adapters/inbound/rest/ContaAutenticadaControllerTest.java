package com.banco.pagamento.adapters.inbound.rest;

import com.banco.pagamento.adapters.inbound.rest.dto.PagamentoRequest;
import com.banco.pagamento.adapters.security.AuditoriaService;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.usecase.ExtratoResultado;
import com.banco.pagamento.application.usecase.PagamentoResultado;
import com.banco.pagamento.ports.inbound.ConsultarBoletoPort;
import com.banco.pagamento.ports.inbound.ConsultarExtratoPort;
import com.banco.pagamento.ports.inbound.ConsultarUsuarioAutenticadoPort;
import com.banco.pagamento.ports.inbound.ProcessarPagamentoPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Controllers com conta autenticada")
class ContaAutenticadaControllerTest {

    @Mock private ProcessarPagamentoPort processarPagamentoPort;
    @Mock private ConsultarBoletoPort consultarBoletoPort;
    @Mock private ConsultarUsuarioAutenticadoPort consultarUsuarioAutenticadoPort;
    @Mock private ConsultarExtratoPort consultarExtratoPort;
    @Mock private AuditoriaService auditoriaService;

    @Test
    @DisplayName("pagamento deve ignorar conta enviada no payload e usar conta autenticada")
    void pagamentoDeveUsarContaAutenticada() {
        PagamentoController controller = new PagamentoController(
            processarPagamentoPort,
            consultarBoletoPort,
            consultarUsuarioAutenticadoPort,
            auditoriaService
        );
        when(consultarUsuarioAutenticadoPort.consultar(1L)).thenReturn(usuario("12345-6"));
        when(processarPagamentoPort.processar(any())).thenReturn(new PagamentoResultado(
            "tx-1",
            "12345-6",
            "23793380896012340900901613951001291070001500000",
            new BigDecimal("250.00"),
            LocalDateTime.now(),
            "APROVADO",
            false
        ));

        controller.pagarBoleto(
            "idem-1",
            new PagamentoRequest("99999-9", "23793380896012340900901613951001291070001500000"),
            new UsernamePasswordAuthenticationToken(1L, null, List.of()),
            new MockHttpServletRequest()
        );

        ArgumentCaptor<com.banco.pagamento.application.usecase.PagamentoComando> captor =
            ArgumentCaptor.forClass(com.banco.pagamento.application.usecase.PagamentoComando.class);
        verify(processarPagamentoPort).processar(captor.capture());
        assertThat(captor.getValue().numeroConta()).isEqualTo("12345-6");
    }

    @Test
    @DisplayName("extrato deve consultar movimentações da conta autenticada")
    void extratoDeveUsarContaAutenticada() {
        ExtratoController controller = new ExtratoController(consultarExtratoPort, consultarUsuarioAutenticadoPort);
        when(consultarUsuarioAutenticadoPort.consultar(2L)).thenReturn(usuario("54321-0"));
        when(consultarExtratoPort.consultar(any())).thenReturn(new ExtratoResultado(List.of(), 0, 0, 10, 0));

        controller.consultar(null, null, null, 0, 10, new UsernamePasswordAuthenticationToken(2L, null, List.of()));

        ArgumentCaptor<com.banco.pagamento.application.usecase.ConsultarExtratoQuery> captor =
            ArgumentCaptor.forClass(com.banco.pagamento.application.usecase.ConsultarExtratoQuery.class);
        verify(consultarExtratoPort).consultar(captor.capture());
        assertThat(captor.getValue().numeroConta()).isEqualTo("54321-0");
    }

    private Usuario usuario(String numeroConta) {
        return Usuario.builder()
            .id(1L)
            .nome("João da Silva")
            .email("joao@bancopagamento.com")
            .cpf("52998224725")
            .numeroConta(numeroConta)
            .build();
    }
}
