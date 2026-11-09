package com.cactech.api.dto;

import com.cactech.api.model.ItemPedido;

import java.math.BigDecimal;

public class ItemPedidoResponse {
    private Long id;
    private Long produtoId;
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;

    public static ItemPedidoResponse fromEntity(ItemPedido itemPedido) {
        ItemPedidoResponse response = new ItemPedidoResponse();
        response.setId(itemPedido.getId());
        response.setProdutoId(itemPedido.getProdutoId());
        response.setNomeProduto(itemPedido.getNomeProduto());
        response.setQuantidade(itemPedido.getQuantidade());
        response.setPrecoUnitario(itemPedido.getPrecoUnitario());
        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}