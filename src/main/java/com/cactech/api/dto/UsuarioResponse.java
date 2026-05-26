package com.cactech.api.dto;

import com.cactech.api.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nomeCompleto,
        String username,
        String email,
        String telefone,
        String cep,
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String avatarUrl
) {

    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getCep(),
                usuario.getRua(),
                usuario.getNumero(),
                usuario.getComplemento(),
                usuario.getBairro(),
                usuario.getCidade(),
                usuario.getEstado(),
                usuario.getAvatarUrl()
        );
    }
}