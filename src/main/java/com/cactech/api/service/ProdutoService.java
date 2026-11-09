package com.cactech.api.service;

import com.cactech.api.constant.ApiMessages;
import com.cactech.api.dto.ProdutoResponse;
import com.cactech.api.exception.RecursoNaoEncontradoException;
import com.cactech.api.model.Departamento;
import com.cactech.api.model.Produto;
import com.cactech.api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Consulta do catalogo de produtos.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    /**
     * Lista produtos com filtros opcionais.
     *
     * @param busca        texto procurado no nome e na categoria (sem diferenciar acentos/maiusculas)
     * @param departamento slug do departamento (ex.: "monitores")
     * @param ordenar      "menor-preco", "maior-preco" ou vazio para a ordem do catalogo
     */
    public List<ProdutoResponse> listar(String busca, String departamento, String ordenar) {
        Departamento filtroDepartamento = null;
        if (departamento != null && !departamento.isBlank()) {
            filtroDepartamento = Departamento.fromSlug(departamento.trim())
                    .orElseThrow(() -> new IllegalArgumentException(ApiMessages.DEPARTAMENTO_INVALIDO));
        }

        List<String> termos = Arrays.stream(normalizar(busca).split("\\s+"))
                .filter(t -> !t.isBlank())
                .toList();

        final Departamento dep = filtroDepartamento;
        Stream<Produto> produtos = produtoRepository.findAllByOrderByIdAsc().stream()
                .filter(p -> dep == null || p.getDepartamento() == dep)
                .filter(p -> {
                    String texto = normalizar(p.getNome() + " " + p.getCategoria());
                    return termos.stream().allMatch(texto::contains);
                });

        if ("menor-preco".equals(ordenar)) {
            produtos = produtos.sorted(Comparator.comparing(Produto::getPreco));
        } else if ("maior-preco".equals(ordenar)) {
            produtos = produtos.sorted(Comparator.comparing(Produto::getPreco).reversed());
        }

        return produtos.map(ProdutoResponse::fromEntity).toList();
    }

    public ProdutoResponse buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(ProdutoResponse::fromEntity)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ApiMessages.PRODUTO_NAO_ENCONTRADO));
    }

    private static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
