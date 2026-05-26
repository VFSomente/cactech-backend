package com.cactech.api.dto;

public class PagamentoCartaoResponse {
    private Long pedidoId;
    private String status;

    public PagamentoCartaoResponse(Long pedidoId, String status) {
        this.pedidoId = pedidoId;
        this.status = status;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public String getStatus() {
        return status;
    }
}