package com.cactech.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RedefinirSenhaRequest {

    @NotBlank(message = "Token eh obrigatorio")
    private String token;

    @NotBlank(message = "Nova senha eh obrigatoria")
    @Size(min = 6, max = 100, message = "A nova senha deve ter pelo menos 6 caracteres")
    private String novaSenha;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}
