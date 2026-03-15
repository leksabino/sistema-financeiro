package com.meufinanceiro.service;

import com.meufinanceiro.exception.SaldoInsuficienteException;
import com.meufinanceiro.model.Transacao;
import com.meufinanceiro.model.TipoTransacao;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransacaoServiceTest {

    private TransacaoService service;

    @BeforeEach
    void setup() {
        // Roda antes de cada teste — cria um service limpo
        service = new TransacaoService();
        service.abrirConta("c1", new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Crédito deve aumentar o saldo")
    void creditoDeveAumentarSaldo() {
        service.processar(new Transacao("t1", "c1",
                new BigDecimal("500.00"), TipoTransacao.CREDITO, LocalDateTime.now()));

        assertEquals(new BigDecimal("1500.00"), service.consultarSaldo("c1"));
    }

    @Test
    @DisplayName("Débito deve reduzir o saldo")
    void debitoDeveReduzirSaldo() {
        service.processar(new Transacao("t1", "c1",
                new BigDecimal("300.00"), TipoTransacao.DEBITO, LocalDateTime.now()));

        assertEquals(new BigDecimal("700.00"), service.consultarSaldo("c1"));
    }

    @Test
    @DisplayName("Deve bloquear débito com saldo insuficiente")
    void deveBloquearDebitoComSaldoInsuficiente() {
        var ex = assertThrows(SaldoInsuficienteException.class, () ->
                service.processar(new Transacao("t1", "c1",
                        new BigDecimal("9999.00"), TipoTransacao.DEBITO, LocalDateTime.now()))
        );

        assertEquals(new BigDecimal("1000.00"), ex.getSaldoAtual());
        assertEquals(new BigDecimal("9999.00"), ex.getValorSolicitado());
    }

    @Test
    @DisplayName("Deve bloquear comportamento suspeito — 3 débitos altos no mesmo dia")
    void deveBloquearComportamentoSuspeito() {
        // 3 débitos altos — ainda permitidos
        for (int i = 1; i <= 3; i++) {
            service.processar(new Transacao("t" + i, "c1",
                    new BigDecimal("1.00"), TipoTransacao.CREDITO, LocalDateTime.now()));
        }
        service.abrirConta("c1", new BigDecimal("99999.00"));

        service.processar(new Transacao("d1", "c1",
                new BigDecimal("600.00"), TipoTransacao.DEBITO, LocalDateTime.now()));
        service.processar(new Transacao("d2", "c1",
                new BigDecimal("600.00"), TipoTransacao.DEBITO, LocalDateTime.now()));
        service.processar(new Transacao("d3", "c1",
                new BigDecimal("600.00"), TipoTransacao.DEBITO, LocalDateTime.now()));

        // 4º débito alto — deve ser bloqueado
        assertThrows(RuntimeException.class, () ->
                service.processar(new Transacao("d4", "c1",
                        new BigDecimal("600.00"), TipoTransacao.DEBITO, LocalDateTime.now()))
        );
    }

    @Test
    @DisplayName("Deve listar transações do cliente")
    void deveListarTransacoes() {
        service.processar(new Transacao("t1", "c1",
                new BigDecimal("100.00"), TipoTransacao.CREDITO, LocalDateTime.now()));
        service.processar(new Transacao("t2", "c1",
                new BigDecimal("50.00"), TipoTransacao.DEBITO, LocalDateTime.now()));

        var lista = service.listarTransacoes("c1");

        assertEquals(2, lista.size());
        assertEquals("t1", lista.get(0).id());
        assertEquals("t2", lista.get(1).id());
    }

    @Test
    @DisplayName("Deve rejeitar cliente inexistente")
    void deveRejeitarClienteInexistente() {
        assertThrows(RuntimeException.class, () ->
                service.consultarSaldo("cliente-fantasma")
        );
    }
}