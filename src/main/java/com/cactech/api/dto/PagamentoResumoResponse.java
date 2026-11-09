package com.cactech.api.dto;

import com.cactech.api.model.MetodoPagamento;
import com.cactech.api.model.Pagamento;
import com.cactech.api.model.PagamentoStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

/**
 * Situacao do pagamento de um pedido, com o codigo Pix ou a linha digitavel
 * do boleto para o cliente consultar depois.
 */
public class PagamentoResumoResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MetodoPagamento metodo;
    private PagamentoStatus status;
    private LocalDateTime data;
    private String codigoPix;
    private String linhaDigitavel;

    public static PagamentoResumoResponse fromEntity(Pagamento pagamento) {
        PagamentoResumoResponse response = new PagamentoResumoResponse();
        response.setMetodo(pagamento.getMetodo());
        response.setStatus(pagamento.getStatus());
        response.setData(pagamento.getDataPagamento());

        JsonNode dados = lerDados(pagamento.getDadosResposta());
        if (dados != null) {
            response.setCodigoPix(texto(dados, "codigoPix"));
            response.setLinhaDigitavel(texto(dados, "linhaDigitavel"));
        }
        return response;
    }

    private static JsonNode lerDados(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readTree(json);
        } catch (Exception ex) {
            return null;
        }
    }

    private static String texto(JsonNode dados, String campo) {
        JsonNode valor = dados.get(campo);
        return valor != null && !valor.isNull() ? valor.asText() : null;
    }

    public MetodoPagamento getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoPagamento metodo) {
        this.metodo = metodo;
    }

    public PagamentoStatus getStatus() {
        return status;
    }

    public void setStatus(PagamentoStatus status) {
        this.status = status;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getCodigoPix() {
        return codigoPix;
    }

    public void setCodigoPix(String codigoPix) {
        this.codigoPix = codigoPix;
    }

    public String getLinhaDigitavel() {
        return linhaDigitavel;
    }

    public void setLinhaDigitavel(String linhaDigitavel) {
        this.linhaDigitavel = linhaDigitavel;
    }
}
