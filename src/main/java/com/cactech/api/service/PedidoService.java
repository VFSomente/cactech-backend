package com.cactech.api.service;

import com.cactech.api.constant.ApiMessages;
import com.cactech.api.dto.CriarPedidoRequest;
import com.cactech.api.dto.ItemPedidoRequest;
import com.cactech.api.dto.PedidoResponse;
import com.cactech.api.exception.RecursoNaoEncontradoException;
import com.cactech.api.model.ItemPedido;
import com.cactech.api.model.Pedido;
import com.cactech.api.model.PedidoStatus;
import com.cactech.api.model.Usuario;
import com.cactech.api.repository.PedidoRepository;
import com.cactech.api.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsavel pela criacao e preparacao de pedidos.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public PedidoResponse criarPedido(CriarPedidoRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RecursoNaoEncontradoException(ApiMessages.USUARIO_AUTENTICADO_NAO_ENCONTRADO);
        }

        Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException(ApiMessages.USUARIO_AUTENTICADO_NAO_ENCONTRADO));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setStatus(PedidoStatus.PENDENTE);
        pedido.setDataCriacao(LocalDateTime.now());

        List<ItemPedido> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoRequest itemRequest : request.getItens()) {
            ItemPedido item = new ItemPedido();
            item.setNomeProduto(itemRequest.getNomeProduto());
            item.setQuantidade(itemRequest.getQuantidade());
            item.setPrecoUnitario(itemRequest.getPrecoUnitario());
            item.setPedido(pedido);
            itens.add(item);

            BigDecimal subtotal = itemRequest.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantidade()));
            total = total.add(subtotal);
        }

        pedido.setItens(itens);
        pedido.setTotal(total);

        Pedido saved = pedidoRepository.save(pedido);
        return PedidoResponse.fromEntity(saved);
    }
}