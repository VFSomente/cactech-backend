package com.cactech.api.controller;

import com.cactech.api.constant.ApiMessages;
import com.cactech.api.dto.EsqueciSenhaRequest;
import com.cactech.api.dto.LoginRequest;
import com.cactech.api.dto.MensagemResponse;
import com.cactech.api.dto.RedefinirSenhaRequest;
import com.cactech.api.service.RedefinicaoSenhaService;
import com.cactech.api.dto.LoginResponse;
import com.cactech.api.dto.RegisterRequest;
import com.cactech.api.dto.UsuarioResponse;
import com.cactech.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RedefinicaoSenhaService redefinicaoSenhaService;

    public AuthController(AuthService authService, RedefinicaoSenhaService redefinicaoSenhaService) {
        this.authService = authService;
        this.redefinicaoSenhaService = redefinicaoSenhaService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<MensagemResponse> esqueciSenha(@Valid @RequestBody EsqueciSenhaRequest request) {
        redefinicaoSenhaService.solicitar(request.getEmail());
        return ResponseEntity.ok(new MensagemResponse(ApiMessages.REDEFINICAO_SOLICITADA));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<MensagemResponse> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        redefinicaoSenhaService.redefinir(request.getToken(), request.getNovaSenha());
        return ResponseEntity.ok(new MensagemResponse(ApiMessages.SENHA_REDEFINIDA));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me() {
        return ResponseEntity.ok(authService.getAuthenticatedUser());
    }
}