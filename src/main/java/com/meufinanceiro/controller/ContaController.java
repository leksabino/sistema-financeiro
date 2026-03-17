package com.meufinanceiro.controller;

import com.meufinanceiro.model.Conta;
import com.meufinanceiro.model.TransacaoEntity;
import com.meufinanceiro.service.ContaService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping("/{id}/abrir")
    public Conta abrirConta(
            @PathVariable String id,
            @RequestParam String titular,
            @RequestParam BigDecimal saldo) {
        return contaService.abrirConta(id, titular, saldo);
    }

    @GetMapping("/{id}/saldo")
    public Map<String, Object> consultarSaldo(@PathVariable String id) {
        var conta = contaService.consultarConta(id);
        return Map.of(
                "clienteId", conta.getId(),
                "titular", conta.getTitular(),
                "saldo", conta.getSaldo()
        );
    }

    @PostMapping("/{id}/transacoes")
    public TransacaoEntity processarTransacao(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return contaService.processar(
                id,
                new BigDecimal(body.get("valor")),
                body.get("tipo")
        );
    }

    @GetMapping("/{id}/transacoes")
    public List<TransacaoEntity> listarTransacoes(@PathVariable String id) {
        return contaService.listarTransacoes(id);
    }
}