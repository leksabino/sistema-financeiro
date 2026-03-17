package com.meufinanceiro.controller;

import com.meufinanceiro.model.Transacao;
import com.meufinanceiro.model.TipoTransacao;
import com.meufinanceiro.service.TransacaoService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final TransacaoService service = new TransacaoService();

    // POST /contas/{id}/abrir?saldo=1000
    @PostMapping("/{id}/abrir")
    public Map<String, Object> abrirConta(
            @PathVariable String id,
            @RequestParam BigDecimal saldo) {

        service.abrirConta(id, saldo);
        return Map.of(
                "mensagem", "Conta aberta com sucesso",
                "clienteId", id,
                "saldoInicial", saldo
        );
    }

    // GET /contas/{id}/saldo
    @GetMapping("/{id}/saldo")
    public Map<String, Object> consultarSaldo(@PathVariable String id) {
        return Map.of(
                "clienteId", id,
                "saldo", service.consultarSaldo(id)
        );
    }

    // POST /contas/{id}/transacoes
    @PostMapping("/{id}/transacoes")
    public Map<String, Object> processarTransacao(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        var transacao = new Transacao(
                java.util.UUID.randomUUID().toString(),
                id,
                new BigDecimal(body.get("valor")),
                TipoTransacao.valueOf(body.get("tipo")),
                LocalDateTime.now()
        );

        service.processar(transacao);
        return Map.of(
                "mensagem", "Transação processada",
                "id", transacao.id(),
                "valor", transacao.valor(),
                "tipo", transacao.tipo()
        );
    }

    // GET /contas/{id}/transacoes
    @GetMapping("/{id}/transacoes")
    public List<Transacao> listarTransacoes(@PathVariable String id) {
        return service.listarTransacoes(id);
    }
}