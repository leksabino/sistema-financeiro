package com.meufinanceiro;

import com.meufinanceiro.model.Transacao;
import com.meufinanceiro.model.TipoTransacao;
import com.meufinanceiro.service.TransacaoService;
import com.meufinanceiro.exception.SaldoInsuficienteException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {

        var service = new TransacaoService();

        // Abre conta com R$1.000 de saldo
        service.abrirConta("cliente-001", new BigDecimal("1000.00"));

        // Crédito
        service.processar(new Transacao("t1", "cliente-001",
                new BigDecimal("500.00"), TipoTransacao.CREDITO, LocalDateTime.now()));

        System.out.println("Após crédito R$500: R$ " + service.consultarSaldo("cliente-001"));

        // Débito
        service.processar(new Transacao("t2", "cliente-001",
                new BigDecimal("200.00"), TipoTransacao.DEBITO, LocalDateTime.now()));

        System.out.println("Após débito R$200:  R$ " + service.consultarSaldo("cliente-001"));

        // Testando saldo insuficiente
        try {
            service.processar(new Transacao("t3", "cliente-001",
                    new BigDecimal("5000.00"), TipoTransacao.DEBITO, LocalDateTime.now()));
        } catch (SaldoInsuficienteException e) {
            System.out.println("Bloqueado: " + e.getMessage());
        }

        // Total por tipo
        System.out.println("\nTotal por tipo:");
        service.totalPorTipo("cliente-001")
                .forEach((tipo, total) ->
                        System.out.println("  " + tipo + ": R$ " + total));

        // Transações acima de R$100
        System.out.println("\nTransações acima de R$100:");
        service.transacoesAcimaDe("cliente-001", new BigDecimal("100.00"))
                .forEach(t -> System.out.println("  " + t.id() + " — R$ " + t.valor()));
    }
}