package com.cactech.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para atualizacao dos dados de perfil do usuario autenticado.
 */
public record AtualizarPerfilRequest(
        @NotBlank(message = "Nome completo e obrigatorio")
        String nomeCompleto,
        String telefone,
        String cep,
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado
) {
}