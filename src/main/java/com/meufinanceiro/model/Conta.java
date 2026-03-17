package com.meufinanceiro.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contas")
public class Conta {

    @Id
    private String id;

    @Column(nullable = false)
    private String titular;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    public Conta() {}

    public Conta(String id, String titular, BigDecimal saldo) {
        this.id       = id;
        this.titular  = titular;
        this.saldo    = saldo;
        this.criadoEm = LocalDateTime.now();
    }

    public String getId()            { return id; }
    public String getTitular()       { return titular; }
    public BigDecimal getSaldo()     { return saldo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}