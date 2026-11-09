package com.cactech.api.service;

import com.cactech.api.dto.ProdutoResponse;
import com.cactech.api.exception.RecursoNaoEncontradoException;
import com.cactech.api.model.Departamento;
import com.cactech.api.model.Produto;
import com.cactech.api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    private ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        produtoService = new ProdutoService(produtoRepository);
        when(produtoRepository.findAllByOrderByIdAsc()).thenReturn(List.of(
                produto(1L, "Placa de vídeo RTX 5060 8GB MSI", "2151.00", Departamento.HARDWARE, "Placa de vídeo"),
                produto(2L, "Memória FURY Beast 16GB DDR4", "469.99", Departamento.HARDWARE, "Memória RAM"),
                produto(3L, "Monitor Gamer AOC 27G2S", "899.99", Departamento.MONITORES, "Monitor")
        ));
    }

    @Test
    void shouldListAllProductsInCatalogOrderWhenNoFilters() {
        List<ProdutoResponse> result = produtoService.listar(null, null, null);

        assertEquals(List.of(1L, 2L, 3L), result.stream().map(ProdutoResponse::getId).toList());
        assertEquals("hardware", result.get(0).getDepartamento());
    }

    @Test
    void shouldSearchIgnoringAccentsAndCase() {
        List<ProdutoResponse> result = produtoService.listar("MEMORIA ddr4", null, null);

        assertEquals(List.of(2L), result.stream().map(ProdutoResponse::getId).toList());
    }

    @Test
    void shouldFilterByDepartmentAndSortByLowestPrice() {
        List<ProdutoResponse> result = produtoService.listar(null, "hardware", "menor-preco");

        assertEquals(List.of(2L, 1L), result.stream().map(ProdutoResponse::getId).toList());
    }

    @Test
    void shouldRejectUnknownDepartment() {
        assertThrows(IllegalArgumentException.class, () -> produtoService.listar(null, "geladeiras", null));
    }

    @Test
    void shouldThrowNotFoundForUnknownId() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> produtoService.buscarPorId(99L));
    }

    private static Produto produto(Long id, String nome, String preco, Departamento departamento, String categoria) {
        Produto produto = new Produto(nome, new BigDecimal(preco), "assets/products/x.jpg", departamento, categoria);
        produto.setId(id);
        return produto;
    }
}
