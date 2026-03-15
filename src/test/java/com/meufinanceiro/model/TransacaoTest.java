package com.meufinanceiro.model;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TransacaoTest {

    @Test
    @DisplayName("Deve criar transação válida")
    void deveCriarTransacaoValida() {
        var t = new Transacao("t1", "c1",
                new BigDecimal("100.00"),
                TipoTransacao.CREDITO,
                LocalDateTime.now());

        assertEquals("t1", t.id());
        assertEquals(new BigDecimal("100.00"), t.valor());
        assertNotNull(t.criadoEm());
    }

    @Test
    @DisplayName("Deve rejeitar valor negativo")
    void deveRejeitarValorNegativo() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transacao("t2", "c1",
                        new BigDecimal("-50.00"),
                        TipoTransacao.DEBITO,
                        LocalDateTime.now())
        );
    }

    @Test
    @DisplayName("Deve rejeitar id nulo")
    void deveRejeitarIdNulo() {
        assertThrows(NullPointerException.class, () ->
                new Transacao(null, "c1",
                        new BigDecimal("100.00"),
                        TipoTransacao.CREDITO,
                        LocalDateTime.now())
        );
    }
}