package com.cactech.api.service;

import com.cactech.api.constant.ApiMessages;
import com.cactech.api.dto.PagamentoBoletoResponse;
import com.cactech.api.dto.PagamentoCartaoResponse;
import com.cactech.api.dto.PagamentoPixResponse;
import com.cactech.api.exception.RecursoNaoEncontradoException;
import com.cactech.api.model.MetodoPagamento;
import com.cactech.api.model.Pagamento;
import com.cactech.api.model.PagamentoStatus;
import com.cactech.api.model.Pedido;
import com.cactech.api.model.PedidoStatus;
import com.cactech.api.repository.PagamentoRepository;
import com.cactech.api.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsavel por processar pagamentos por diferentes metodos.
 */
@Service
public class PagamentoService {

    private final PedidoRepository pedidoRepository;
    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PedidoRepository pedidoRepository, PagamentoRepository pagamentoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    public PagamentoPixResponse processarPix(Long pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);
        String codigoPix = "PIX-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();

        Pagamento pagamento = obterOuCriarPagamento(pedido);
        pagamento.setMetodo(MetodoPagamento.PIX);
        pagamento.setStatus(PagamentoStatus.PENDENTE);
        pagamento.setDadosResposta("{\"codigoPix\":\"" + codigoPix + "\"}");
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamentoRepository.save(pagamento);

        return new PagamentoPixResponse(pedidoId, pagamento.getStatus().name(), codigoPix);
    }

    public PagamentoBoletoResponse processarBoleto(Long pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);
        String linhaDigitavel = gerarLinhaDigitavelMock();

        Pagamento pagamento = obterOuCriarPagamento(pedido);
        pagamento.setMetodo(MetodoPagamento.BOLETO);
        pagamento.setStatus(PagamentoStatus.PENDENTE);
        pagamento.setDadosResposta("{\"linhaDigitavel\":\"" + linhaDigitavel + "\"}");
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamentoRepository.save(pagamento);

        return new PagamentoBoletoResponse(pedidoId, pagamento.getStatus().name(), linhaDigitavel);
    }

    public PagamentoCartaoResponse processarCartao(Long pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);

        Pagamento pagamento = obterOuCriarPagamento(pedido);
        pagamento.setMetodo(MetodoPagamento.CARTAO);
        pagamento.setStatus(PagamentoStatus.APROVADO);
        pagamento.setDadosResposta("{\"status\":\"APROVADO\"}");
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamentoRepository.save(pagamento);

        pedido.setStatus(PedidoStatus.PAGO);
        pedidoRepository.save(pedido);

        return new PagamentoCartaoResponse(pedidoId, pagamento.getStatus().name());
    }

    private Pedido buscarPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ApiMessages.PEDIDO_NAO_ENCONTRADO));
    }

    private Pagamento obterOuCriarPagamento(Pedido pedido) {
        return pagamentoRepository.findByPedidoId(pedido.getId())
                .orElseGet(() -> {
                    Pagamento novoPagamento = new Pagamento();
                    novoPagamento.setPedido(pedido);
                    return novoPagamento;
                });
    }

    private String gerarLinhaDigitavelMock() {
        return "23790.12345 60000.123456 78000.123456 1 " + (System.currentTimeMillis() % 10000000000L);
    }
}