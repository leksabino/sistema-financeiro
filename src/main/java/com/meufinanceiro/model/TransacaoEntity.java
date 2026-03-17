package com.meufinanceiro.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
public class TransacaoEntity {

    @Id
    private String id;

    @Column(name = "conta_id", nullable = false)
    private String contaId;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private String tipo;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    public TransacaoEntity() {}

    public TransacaoEntity(String id, String contaId,
                           BigDecimal valor, String tipo) {
        this.id       = id;
        this.contaId  = contaId;
        this.valor    = valor;
        this.tipo     = tipo;
        this.criadoEm = LocalDateTime.now();
    }

    public String getId()              { return id; }
    public String getContaId()         { return contaId; }
    public BigDecimal getValor()       { return valor; }
    public String getTipo()            { return tipo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}