package com.cactech.api.repository;

import com.cactech.api.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByPedidoId(Long pedidoId);

    List<Pagamento> findByPedidoIdIn(Collection<Long> pedidoIds);
}
