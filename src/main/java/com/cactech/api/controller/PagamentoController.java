package com.cactech.api.controller;

import com.cactech.api.dto.PagamentoBoletoResponse;
import com.cactech.api.dto.PagamentoCartaoResponse;
import com.cactech.api.dto.PagamentoPixResponse;
import com.cactech.api.service.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/pix/{pedidoId}")
    public ResponseEntity<PagamentoPixResponse> processarPix(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.processarPix(pedidoId));
    }

    @PostMapping("/boleto/{pedidoId}")
    public ResponseEntity<PagamentoBoletoResponse> processarBoleto(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.processarBoleto(pedidoId));
    }

    @PostMapping("/cartao/{pedidoId}")
    public ResponseEntity<PagamentoCartaoResponse> processarCartao(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.processarCartao(pedidoId));
    }
}