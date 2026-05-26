package com.cactech.api.dto;

public class PagamentoPixResponse {
    private Long pedidoId;
    private String status;
    private String codigoPix;

    public PagamentoPixResponse(Long pedidoId, String status, String codigoPix) {
        this.pedidoId = pedidoId;
        this.status = status;
        this.codigoPix = codigoPix;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public String getStatus() {
        return status;
    }

    public String getCodigoPix() {
        return codigoPix;
    }
}