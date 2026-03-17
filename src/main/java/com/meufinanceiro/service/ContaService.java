package com.meufinanceiro.service;

import com.meufinanceiro.exception.SaldoInsuficienteException;
import com.meufinanceiro.model.Conta;
import com.meufinanceiro.model.TransacaoEntity;
import com.meufinanceiro.repository.ContaRepository;
import com.meufinanceiro.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;

    public ContaService(ContaRepository contaRepository,
                        TransacaoRepository transacaoRepository) {
        this.contaRepository      = contaRepository;
        this.transacaoRepository  = transacaoRepository;
    }

    @Transactional
    public Conta abrirConta(String id, String titular, BigDecimal saldoInicial) {
        if (contaRepository.existsById(id))
            throw new RuntimeException("Conta já existe: " + id);
        var conta = new Conta(id, titular, saldoInicial);
        return contaRepository.save(conta);
    }

    public Conta consultarConta(String id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada: " + id));
    }

    @Transactional
    public TransacaoEntity processar(String contaId, BigDecimal valor, String tipo) {
        var conta = consultarConta(contaId);

        if (tipo.equals("DEBITO") || tipo.equals("TRANSFERENCIA")) {
            if (valor.compareTo(conta.getSaldo()) > 0)
                throw new SaldoInsuficienteException(conta.getSaldo(), valor);
            conta.setSaldo(conta.getSaldo().subtract(valor));
        } else {
            conta.setSaldo(conta.getSaldo().add(valor));
        }

        contaRepository.save(conta);

        var transacao = new TransacaoEntity(
                UUID.randomUUID().toString(),
                contaId, valor, tipo
        );
        return transacaoRepository.save(transacao);
    }

    public List<TransacaoEntity> listarTransacoes(String contaId) {
        consultarConta(contaId);
        return transacaoRepository.findByContaId(contaId);
    }
}