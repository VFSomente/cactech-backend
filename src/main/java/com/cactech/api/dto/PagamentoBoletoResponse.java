package com.cactech.api.dto;

public class PagamentoBoletoResponse {
    private Long pedidoId;
    private String status;
    private String linhaDigitavel;

    public PagamentoBoletoResponse(Long pedidoId, String status, String linhaDigitavel) {
        this.pedidoId = pedidoId;
        this.status = status;
        this.linhaDigitavel = linhaDigitavel;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public String getStatus() {
        return status;
    }

    public String getLinhaDigitavel() {
        return linhaDigitavel;
    }
}