package com.meufinanceiro.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {

    private final BigDecimal saldoAtual;
    private final BigDecimal valorSolicitado;

    public SaldoInsuficienteException(BigDecimal saldoAtual, BigDecimal valorSolicitado) {
        super("Saldo insuficiente. Disponível: R$ %s, Solicitado: R$ %s"
                .formatted(saldoAtual, valorSolicitado));
        this.saldoAtual      = saldoAtual;
        this.valorSolicitado = valorSolicitado;
    }

    public BigDecimal getSaldoAtual()      { return saldoAtual; }
    public BigDecimal getValorSolicitado() { return valorSolicitado; }
}