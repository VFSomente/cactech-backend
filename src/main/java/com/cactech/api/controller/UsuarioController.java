package com.cactech.api.controller;

import com.cactech.api.dto.AtualizarPerfilRequest;
import com.cactech.api.dto.UsuarioResponse;
import com.cactech.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller de perfil e avatar de usuario.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Atualiza dados de perfil do usuario autenticado.
     *
     * @param request dados atualizaveis de perfil
     * @return usuario atualizado
     */
    @PutMapping("/perfil")
    public ResponseEntity<UsuarioResponse> atualizarPerfil(@Valid @RequestBody AtualizarPerfilRequest request) {
        return ResponseEntity.ok(usuarioService.atualizarPerfil(request));
    }

    /**
     * Realiza upload de avatar para o usuario autenticado.
     *
     * @param file imagem enviada em multipart/form-data
     * @return usuario com avatar atualizado
     */
    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<UsuarioResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(usuarioService.uploadAvatar(file));
    }

}