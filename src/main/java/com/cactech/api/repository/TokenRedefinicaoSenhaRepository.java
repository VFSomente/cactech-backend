package com.cactech.api.repository;

import com.cactech.api.model.TokenRedefinicaoSenha;
import com.cactech.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRedefinicaoSenhaRepository extends JpaRepository<TokenRedefinicaoSenha, Long> {

    Optional<TokenRedefinicaoSenha> findByTokenHash(String tokenHash);

    List<TokenRedefinicaoSenha> findByUsuarioAndUsadoEmIsNull(Usuario usuario);
}
