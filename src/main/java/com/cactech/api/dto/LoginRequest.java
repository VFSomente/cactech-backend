package com.cactech.api.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username eh obrigatorio")
    private String username;

    @NotBlank(message = "Senha eh obrigatoria")
    private String senha;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}