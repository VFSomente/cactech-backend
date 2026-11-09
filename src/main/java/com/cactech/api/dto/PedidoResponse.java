package com.cactech.api.dto;

import com.cactech.api.model.Pagamento;
import com.cactech.api.model.Pedido;
import com.cactech.api.model.PedidoStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {
    private Long id;
    private Long usuarioId;
    private List<ItemPedidoResponse> itens;
    private BigDecimal total;
    private PedidoStatus status;
    private LocalDateTime dataCriacao;
    /** Nulo enquanto nenhuma forma de pagamento foi escolhida. */
    private PagamentoResumoResponse pagamento;

    public static PedidoResponse fromEntity(Pedido pedido, Pagamento pagamento) {
        PedidoResponse response = fromEntity(pedido);
        if (pagamento != null) {
            response.setPagamento(PagamentoResumoResponse.fromEntity(pagamento));
        }
        return response;
    }

    public PagamentoResumoResponse getPagamento() {
        return pagamento;
    }

    public void setPagamento(PagamentoResumoResponse pagamento) {
        this.pagamento = pagamento;
    }

    public static PedidoResponse fromEntity(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setUsuarioId(pedido.getUsuario().getId());
        response.setItens(pedido.getItens().stream().map(ItemPedidoResponse::fromEntity).toList());
        response.setTotal(pedido.getTotal());
        response.setStatus(pedido.getStatus());
        response.setDataCriacao(pedido.getDataCriacao());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<ItemPedidoResponse> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoResponse> itens) {
        this.itens = itens;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public PedidoStatus getStatus() {
        return status;
    }

    public void setStatus(PedidoStatus status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}