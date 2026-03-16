package com.meufinanceiro.service;

import com.meufinanceiro.exception.SaldoInsuficienteException;
import com.meufinanceiro.model.Transacao;
import com.meufinanceiro.model.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TransacaoService {

    private final Map<String, List<Transacao>> porCliente = new HashMap<>();
    private final Map<String, BigDecimal> saldos          = new HashMap<>();

    public void abrirConta(String clienteId, BigDecimal saldoInicial) {
        saldos.put(clienteId, saldoInicial);
        porCliente.put(clienteId, new ArrayList<>());
    }

    public void processar(Transacao t) {
        validarCliente(t.clienteId());
        verificarTransacoesSuspeitas(t);
        verificarLimiteDiarioTransferencia(t);

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

    public BigDecimal consultarSaldo(String clienteId) {
        validarCliente(clienteId);
        return saldos.get(clienteId);
    }

    public List<Transacao> listarTransacoes(String clienteId) {
        validarCliente(clienteId);
        return Collections.unmodifiableList(porCliente.get(clienteId));
    }

    public Map<TipoTransacao, BigDecimal> totalPorTipo(String clienteId) {
        validarCliente(clienteId);
        return porCliente.get(clienteId).stream()
                .collect(Collectors.groupingBy(
                        Transacao::tipo,
                        Collectors.reducing(BigDecimal.ZERO, Transacao::valor, BigDecimal::add)
                ));
    }

    public List<Transacao> transacoesAcimaDe(String clienteId, BigDecimal limite) {
        validarCliente(clienteId);
        return porCliente.get(clienteId).stream()
                .filter(t -> t.valor().compareTo(limite) > 0)
                .sorted((a, b) -> b.valor().compareTo(a.valor()))
                .toList();
    }

    private void verificarTransacoesSuspeitas(Transacao nova) {
        if (nova.tipo() != TipoTransacao.DEBITO) return;
        if (nova.valor().compareTo(new BigDecimal("500")) <= 0) return;

        long altasHoje = porCliente.getOrDefault(nova.clienteId(), List.of())
                .stream()
                .filter(t -> t.tipo() == TipoTransacao.DEBITO)
                .filter(t -> t.valor().compareTo(new BigDecimal("500")) > 0)
                .filter(t -> t.criadoEm().toLocalDate().equals(LocalDate.now()))
                .count();

        if (altasHoje >= 3)
            throw new RuntimeException("Operação bloqueada: comportamento suspeito detectado");
    }

    private void verificarLimiteDiarioTransferencia(Transacao nova) {
        if (nova.tipo() != TipoTransacao.TRANSFERENCIA) return;

        BigDecimal limite = new BigDecimal("5000.00");

        BigDecimal totalHoje = porCliente
                .getOrDefault(nova.clienteId(), List.of())
                .stream()
                .filter(t -> t.tipo() == TipoTransacao.TRANSFERENCIA)
                .filter(t -> t.criadoEm().toLocalDate().equals(LocalDate.now()))
                .map(Transacao::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalHoje.add(nova.valor()).compareTo(limite) > 0)
            throw new RuntimeException(
                    "Limite diário de transferências atingido. " +
                            "Limite: R$ " + limite +
                            ", Utilizado: R$ " + totalHoje +
                            ", Solicitado: R$ " + nova.valor()
            );
    }

    private void validarCliente(String clienteId) {
        if (!saldos.containsKey(clienteId))
            throw new RuntimeException("Cliente não encontrado: " + clienteId);
    }
}