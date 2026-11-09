package com.cactech.api.service;

import com.cactech.api.constant.ApiMessages;
import com.cactech.api.dto.CriarPedidoRequest;
import com.cactech.api.dto.ItemPedidoRequest;
import com.cactech.api.dto.PedidoResponse;
import com.cactech.api.exception.RecursoNaoEncontradoException;
import com.cactech.api.model.ItemPedido;
import com.cactech.api.model.Pagamento;
import com.cactech.api.model.Pedido;
import com.cactech.api.model.PedidoStatus;
import com.cactech.api.model.Usuario;
import com.cactech.api.repository.PagamentoRepository;
import com.cactech.api.repository.PedidoRepository;
import com.cactech.api.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service responsavel pela criacao e consulta de pedidos do usuario autenticado.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagamentoRepository pagamentoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         PagamentoRepository pagamentoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    public PedidoResponse criarPedido(CriarPedidoRequest request) {
        Usuario usuario = usuarioAutenticado();

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setStatus(PedidoStatus.PENDENTE);
        pedido.setDataCriacao(LocalDateTime.now());

        List<ItemPedido> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoRequest itemRequest : request.getItens()) {
            ItemPedido item = new ItemPedido();
            item.setProdutoId(itemRequest.getProdutoId());
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

    /** Pedidos do usuario autenticado, do mais recente para o mais antigo. */
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarMeusPedidos() {
        Usuario usuario = usuarioAutenticado();
        List<Pedido> pedidos = pedidoRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuario.getId());
        if (pedidos.isEmpty()) {
            return List.of();
        }

        Map<Long, Pagamento> pagamentos = pagamentoRepository
                .findByPedidoIdIn(pedidos.stream().map(Pedido::getId).toList())
                .stream()
                .collect(Collectors.toMap(p -> p.getPedido().getId(), Function.identity()));

        return pedidos.stream()
                .map(p -> PedidoResponse.fromEntity(p, pagamentos.get(p.getId())))
                .toList();
    }

    /** Detalhe de um pedido; pedidos de outros usuarios sao tratados como inexistentes. */
    @Transactional(readOnly = true)
    public PedidoResponse buscarMeuPedido(Long id) {
        Usuario usuario = usuarioAutenticado();
        Pedido pedido = pedidoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(ApiMessages.PEDIDO_NAO_ENCONTRADO));
        Pagamento pagamento = pagamentoRepository.findByPedidoId(pedido.getId()).orElse(null);
        return PedidoResponse.fromEntity(pedido, pagamento);
    }

    private Usuario usuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RecursoNaoEncontradoException(ApiMessages.USUARIO_AUTENTICADO_NAO_ENCONTRADO);
        }
        return usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException(ApiMessages.USUARIO_AUTENTICADO_NAO_ENCONTRADO));
    }
}
