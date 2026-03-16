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
        service.abrirConta("c1", new BigDecimal("99999.00"));

        service.processar(new Transacao("d1", "c1",
                new BigDecimal("600.00"), TipoTransacao.DEBITO, LocalDateTime.now()));
        service.processar(new Transacao("d2", "c1",
                new BigDecimal("600.00"), TipoTransacao.DEBITO, LocalDateTime.now()));
        service.processar(new Transacao("d3", "c1",
                new BigDecimal("600.00"), TipoTransacao.DEBITO, LocalDateTime.now()));

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

    @Test
    @DisplayName("Deve bloquear transferência acima do limite diário")
    void deveBloquearTransferenciaAcimaDoLimiteDiario() {
        service.abrirConta("c1", new BigDecimal("99999.00"));

        service.processar(new Transacao("tf1", "c1",
                new BigDecimal("1000.00"), TipoTransacao.TRANSFERENCIA, LocalDateTime.now()));
        service.processar(new Transacao("tf2", "c1",
                new BigDecimal("1000.00"), TipoTransacao.TRANSFERENCIA, LocalDateTime.now()));
        service.processar(new Transacao("tf3", "c1",
                new BigDecimal("1000.00"), TipoTransacao.TRANSFERENCIA, LocalDateTime.now()));

        assertThrows(RuntimeException.class, () ->
                service.processar(new Transacao("tf4", "c1",
                        new BigDecimal("2500.00"), TipoTransacao.TRANSFERENCIA, LocalDateTime.now()))
        );
    }

    @Test
    @DisplayName("Deve permitir transferências dentro do limite diário")
    void devePermitirTransferenciasDentroDoLimite() {
        service.abrirConta("c1", new BigDecimal("99999.00"));

        service.processar(new Transacao("tf1", "c1",
                new BigDecimal("2000.00"), TipoTransacao.TRANSFERENCIA, LocalDateTime.now()));
        service.processar(new Transacao("tf2", "c1",
                new BigDecimal("2999.00"), TipoTransacao.TRANSFERENCIA, LocalDateTime.now()));

        // 99999 - 2000 - 2999 = 95000
        assertEquals(new BigDecimal("95000.00"), service.consultarSaldo("c1"));
    }
}