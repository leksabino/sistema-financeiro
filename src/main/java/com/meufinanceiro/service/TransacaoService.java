package com.meufinanceiro.service;

import com.meufinanceiro.exception.SaldoInsuficienteException;
import com.meufinanceiro.model.Transacao;
import com.meufinanceiro.model.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TransacaoService {

    // Simula um banco de dados em memória
    private final Map<String, List<Transacao>> porCliente = new HashMap<>();
    private final Map<String, BigDecimal> saldos          = new HashMap<>();

    // Registra saldo inicial de um cliente
    public void abrirConta(String clienteId, BigDecimal saldoInicial) {
        saldos.put(clienteId, saldoInicial);
        porCliente.put(clienteId, new ArrayList<>());
    }

    // Processa uma transação
    public void processar(Transacao t) {
        validarCliente(t.clienteId());
        verificarTransacoesSuspeitas(t);

        BigDecimal saldoAtual = saldos.get(t.clienteId());

        if (t.tipo() == TipoTransacao.DEBITO || t.tipo() == TipoTransacao.TRANSFERENCIA) {
            if (t.valor().compareTo(saldoAtual) > 0)
                throw new SaldoInsuficienteException(saldoAtual, t.valor());
            saldos.put(t.clienteId(), saldoAtual.subtract(t.valor()));
        } else {
            saldos.put(t.clienteId(), saldoAtual.add(t.valor()));
        }

        porCliente.get(t.clienteId()).add(t);
    }

    // Retorna saldo atual
    public BigDecimal consultarSaldo(String clienteId) {
        validarCliente(clienteId);
        return saldos.get(clienteId);
    }

    // Lista todas as transações de um cliente
    public List<Transacao> listarTransacoes(String clienteId) {
        validarCliente(clienteId);
        return Collections.unmodifiableList(porCliente.get(clienteId));
    }

    // Total gasto por tipo (usa Streams)
    public Map<TipoTransacao, BigDecimal> totalPorTipo(String clienteId) {
        validarCliente(clienteId);
        return porCliente.get(clienteId).stream()
                .collect(Collectors.groupingBy(
                        Transacao::tipo,
                        Collectors.reducing(BigDecimal.ZERO, Transacao::valor, BigDecimal::add)
                ));
    }

    // Busca transações acima de um valor (usa Streams)
    public List<Transacao> transacoesAcimaDe(String clienteId, BigDecimal limite) {
        validarCliente(clienteId);
        return porCliente.get(clienteId).stream()
                .filter(t -> t.valor().compareTo(limite) > 0)
                .sorted((a, b) -> b.valor().compareTo(a.valor()))
                .toList();
    }

    // Regra antifraude: mais de 3 débitos acima de R$500 no mesmo dia
    private void verificarTransacoesSuspeitas(Transacao nova) {
        if (nova.tipo() != TipoTransacao.DEBITO) return;
        if (nova.valor().compareTo(new BigDecimal("500")) <= 0) return;

        List<Transacao> historico = porCliente.getOrDefault(nova.clienteId(), List.of());

        long altasHoje = historico.stream()
                .filter(t -> t.tipo() == TipoTransacao.DEBITO)
                .filter(t -> t.valor().compareTo(new BigDecimal("500")) > 0)
                .filter(t -> t.criadoEm().toLocalDate().equals(LocalDate.now()))
                .count();

        if (altasHoje >= 3)
            throw new RuntimeException("Operação bloqueada: comportamento suspeito detectado");
    }

    private void validarCliente(String clienteId) {
        if (!saldos.containsKey(clienteId))
            throw new RuntimeException("Cliente não encontrado: " + clienteId);
    }
}