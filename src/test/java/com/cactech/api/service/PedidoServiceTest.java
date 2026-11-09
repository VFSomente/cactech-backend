package com.cactech.api.service;

import com.cactech.api.dto.CriarPedidoRequest;
import com.cactech.api.dto.ItemPedidoRequest;
import com.cactech.api.dto.PedidoResponse;
import com.cactech.api.exception.RecursoNaoEncontradoException;
import com.cactech.api.model.MetodoPagamento;
import com.cactech.api.model.Pagamento;
import com.cactech.api.model.PagamentoStatus;
import com.cactech.api.model.Pedido;
import com.cactech.api.model.PedidoStatus;
import com.cactech.api.model.Usuario;
import com.cactech.api.repository.PagamentoRepository;
import com.cactech.api.repository.PedidoRepository;
import com.cactech.api.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldListOnlyAuthenticatedUserOrdersWithPaymentInfo() {
        PedidoService pedidoService = new PedidoService(pedidoRepository, usuarioRepository, pagamentoRepository);
        Usuario usuario = autenticar("usuarioteste", 10L);

        Pedido pedido = pedido(5L, usuario, PedidoStatus.PENDENTE);
        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setMetodo(MetodoPagamento.PIX);
        pagamento.setStatus(PagamentoStatus.PENDENTE);
        pagamento.setDadosResposta("{\"codigoPix\":\"PIX-123\"}");

        when(pedidoRepository.findByUsuarioIdOrderByDataCriacaoDesc(10L)).thenReturn(List.of(pedido));
        when(pagamentoRepository.findByPedidoIdIn(List.of(5L))).thenReturn(List.of(pagamento));

        List<PedidoResponse> result = pedidoService.listarMeusPedidos();

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getId());
        assertEquals(MetodoPagamento.PIX, result.get(0).getPagamento().getMetodo());
        assertEquals("PIX-123", result.get(0).getPagamento().getCodigoPix());
    }

    @Test
    void shouldNotExposeOrderFromAnotherUser() {
        PedidoService pedidoService = new PedidoService(pedidoRepository, usuarioRepository, pagamentoRepository);
        autenticar("usuarioteste", 10L);

        when(pedidoRepository.findByIdAndUsuarioId(99L, 10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> pedidoService.buscarMeuPedido(99L));
    }

    private Usuario autenticar(String username, Long id) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername(username);
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));
        return usuario;
    }

    private static Pedido pedido(Long id, Usuario usuario, PedidoStatus status) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setUsuario(usuario);
        pedido.setStatus(status);
        pedido.setTotal(new BigDecimal("100.00"));
        pedido.setDataCriacao(LocalDateTime.now());
        return pedido;
    }

    @Test
    void shouldCreateOrderForAuthenticatedUserAndCalculateTotal() {
        PedidoService pedidoService = new PedidoService(pedidoRepository, usuarioRepository, pagamentoRepository);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("usuarioteste", null)
        );

        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setUsername("usuarioteste");

        when(usuarioRepository.findByUsername("usuarioteste")).thenReturn(Optional.of(usuario));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ItemPedidoRequest item1 = new ItemPedidoRequest();
        item1.setNomeProduto("Produto A");
        item1.setQuantidade(2);
        item1.setPrecoUnitario(new BigDecimal("10.00"));

        ItemPedidoRequest item2 = new ItemPedidoRequest();
        item2.setNomeProduto("Produto B");
        item2.setQuantidade(1);
        item2.setPrecoUnitario(new BigDecimal("5.50"));

        CriarPedidoRequest request = new CriarPedidoRequest();
        request.setItens(List.of(item1, item2));

        PedidoResponse response = pedidoService.criarPedido(request);

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());
        Pedido savedPedido = captor.getValue();

        assertEquals(new BigDecimal("25.50"), savedPedido.getTotal());
        assertEquals(2, savedPedido.getItens().size());
        assertEquals(1L, response.getId());
        assertEquals(new BigDecimal("25.50"), response.getTotal());
    }
}