package com.banco.pagamento.adapters.outbound.persistence.entity;

import com.banco.pagamento.application.domain.StatusBoleto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_boleto")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoletoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_barra", nullable = false, unique = true, length = 48)
    private String codigoBarra;

    @Column(name = "beneficiario", nullable = false, length = 100)
    private String beneficiario;

    @Column(name = "pagador", nullable = false, length = 100)
    private String pagador;

    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusBoleto status;
}
