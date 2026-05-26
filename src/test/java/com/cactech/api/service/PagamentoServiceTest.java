package com.cactech.api.service;

import com.cactech.api.dto.PagamentoBoletoResponse;
import com.cactech.api.dto.PagamentoCartaoResponse;
import com.cactech.api.dto.PagamentoPixResponse;
import com.cactech.api.model.Pagamento;
import com.cactech.api.model.PagamentoStatus;
import com.cactech.api.model.Pedido;
import com.cactech.api.model.PedidoStatus;
import com.cactech.api.repository.PagamentoRepository;
import com.cactech.api.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Test
    void shouldProcessPixAndReturnMockCode() {
        PagamentoService pagamentoService = new PagamentoService(pedidoRepository, pagamentoRepository);

        Pedido pedido = new Pedido();
        pedido.setId(1L);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pagamentoRepository.findByPedidoId(1L)).thenReturn(Optional.empty());
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PagamentoPixResponse response = pagamentoService.processarPix(1L);

        ArgumentCaptor<Pagamento> captor = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentoRepository).save(captor.capture());
        Pagamento saved = captor.getValue();

        assertEquals(PagamentoStatus.PENDENTE, saved.getStatus());
        assertTrue(response.getCodigoPix().startsWith("PIX-"));
        assertEquals("PENDENTE", response.getStatus());
    }

    @Test
    void shouldProcessBoletoAndReturnMockLine() {
        PagamentoService pagamentoService = new PagamentoService(pedidoRepository, pagamentoRepository);

        Pedido pedido = new Pedido();
        pedido.setId(2L);

        when(pedidoRepository.findById(2L)).thenReturn(Optional.of(pedido));
        when(pagamentoRepository.findByPedidoId(2L)).thenReturn(Optional.empty());
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PagamentoBoletoResponse response = pagamentoService.processarBoleto(2L);

        assertTrue(response.getLinhaDigitavel().contains("23790"));
        assertEquals("PENDENTE", response.getStatus());
    }

    @Test
    void shouldProcessCardAndApprovePayment() {
        PagamentoService pagamentoService = new PagamentoService(pedidoRepository, pagamentoRepository);

        Pedido pedido = new Pedido();
        pedido.setId(3L);
        pedido.setStatus(PedidoStatus.PENDENTE);

        when(pedidoRepository.findById(3L)).thenReturn(Optional.of(pedido));
        when(pagamentoRepository.findByPedidoId(3L)).thenReturn(Optional.empty());
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PagamentoCartaoResponse response = pagamentoService.processarCartao(3L);

        assertEquals("APROVADO", response.getStatus());
        assertEquals(PedidoStatus.PAGO, pedido.getStatus());
        verify(pedidoRepository).save(pedido);
    }
}