package com.meufinanceiro.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public record Transacao(
        String id,
        String clienteId,
        BigDecimal valor,
        TipoTransacao tipo,
        LocalDateTime criadoEm
) {
    public Transacao {
        Objects.requireNonNull(id,        "id é obrigatório");
        Objects.requireNonNull(clienteId, "clienteId é obrigatório");
        Objects.requireNonNull(valor,     "valor é obrigatório");
        Objects.requireNonNull(tipo,      "tipo é obrigatório");
        if (valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        if (criadoEm == null)
            criadoEm = LocalDateTime.now();
    }
}