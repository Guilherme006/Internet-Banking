package com.banco.pagamento.adapters.outbound.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_conta")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_conta", nullable = false, unique = true, length = 10)
    private String numeroConta;

    @Column(name = "agencia", nullable = false, length = 10)
    private String agencia;

    @Column(name = "titular", nullable = false, length = 100)
    private String titular;

    @Column(name = "saldo", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @Version
    @Column(name = "versao")
    private Long versao;
}
