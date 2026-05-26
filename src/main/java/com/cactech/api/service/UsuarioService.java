package com.cactech.api.service;

import com.cactech.api.constant.ApiMessages;
import com.cactech.api.dto.AtualizarPerfilRequest;
import com.cactech.api.dto.UsuarioResponse;
import com.cactech.api.exception.ArquivoInvalidoException;
import com.cactech.api.exception.RecursoNaoEncontradoException;
import com.cactech.api.model.Usuario;
import com.cactech.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

/**
 * Service responsavel pelas operacoes de perfil e avatar do usuario autenticado.
 */
@Service
public class UsuarioService {

    private static final Map<String, String> CONTENT_TYPE_TO_EXTENSION = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/gif", ".gif",
            "image/webp", ".webp"
    );

    private final UsuarioRepository usuarioRepository;

    @Value("${app.upload.avatar-dir:uploads/avatars}")
    private String avatarDir;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Atualiza os dados de perfil do usuario autenticado.
     *
     * @param request dados de perfil
     * @return dados atualizados
     */
    public UsuarioResponse atualizarPerfil(AtualizarPerfilRequest request) {
        Usuario usuario = getUsuarioAutenticado();
        usuario.setNomeCompleto(request.nomeCompleto());
        usuario.setTelefone(request.telefone());
        usuario.setCep(request.cep());
        usuario.setRua(request.rua());
        usuario.setNumero(request.numero());
        usuario.setComplemento(request.complemento());
        usuario.setBairro(request.bairro());
        usuario.setCidade(request.cidade());
        usuario.setEstado(request.estado());
        Usuario saved = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(saved);
    }

    /**
     * Salva o avatar do usuario autenticado em disco e atualiza referencia.
     *
     * @param file arquivo de imagem
     * @return dados do usuario com novo avatar
     */
    public UsuarioResponse uploadAvatar(MultipartFile file) {
        validarImagem(file);
        String extension = resolverExtensao(file);
        String fileName = UUID.randomUUID() + extension;
        Path targetDir = Paths.get(avatarDir).toAbsolutePath().normalize();
        Path targetFile = targetDir.resolve(fileName);

        try {
            Files.createDirectories(targetDir);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException(ApiMessages.ERRO_SALVAR_AVATAR, ex);
        }

        Usuario usuario = getUsuarioAutenticado();
        usuario.setAvatarUrl("/api/usuarios/avatar/" + fileName);
        Usuario saved = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(saved);
    }

    private void validarImagem(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ArquivoInvalidoException(ApiMessages.ARQUIVO_IMAGEM_OBRIGATORIO);
        }
        if (!StringUtils.hasText(file.getContentType()) || !CONTENT_TYPE_TO_EXTENSION.containsKey(file.getContentType())) {
            throw new ArquivoInvalidoException(ApiMessages.TIPO_IMAGEM_NAO_SUPORTADO);
        }
    }

    private String resolverExtensao(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !CONTENT_TYPE_TO_EXTENSION.containsKey(contentType)) {
            throw new ArquivoInvalidoException(ApiMessages.TIPO_IMAGEM_NAO_SUPORTADO);
        }
        return CONTENT_TYPE_TO_EXTENSION.get(contentType);
    }

    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new RecursoNaoEncontradoException(ApiMessages.USUARIO_AUTENTICADO_NAO_ENCONTRADO);
        }
        return usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException(ApiMessages.USUARIO_AUTENTICADO_NAO_ENCONTRADO));
    }
}