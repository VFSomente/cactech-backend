package com.cactech.api.service;

import com.cactech.api.dto.CriarPedidoRequest;
import com.cactech.api.dto.ItemPedidoRequest;
import com.cactech.api.dto.PedidoResponse;
import com.cactech.api.model.Pedido;
import com.cactech.api.model.Usuario;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateOrderForAuthenticatedUserAndCalculateTotal() {
        PedidoService pedidoService = new PedidoService(pedidoRepository, usuarioRepository);

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