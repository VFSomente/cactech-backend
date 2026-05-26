package com.cactech.api.service;

import com.cactech.api.constant.ApiMessages;
import com.cactech.api.dto.LoginRequest;
import com.cactech.api.dto.LoginResponse;
import com.cactech.api.dto.RegisterRequest;
import com.cactech.api.dto.UsuarioResponse;
import com.cactech.api.exception.CredenciaisInvalidasException;
import com.cactech.api.exception.RecursoNaoEncontradoException;
import com.cactech.api.exception.UsuarioJaExisteException;
import com.cactech.api.model.Usuario;
import com.cactech.api.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsavel por regras de autenticacao e cadastro de usuarios.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public UsuarioResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new UsuarioJaExisteException(ApiMessages.EMAIL_JA_CADASTRADO);
        }
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new UsuarioJaExisteException(ApiMessages.USERNAME_JA_CADASTRADO);
        }

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(request.getNomeCompleto());
        usuario.setUsername(request.getUsername());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setEmail(request.getEmail());
        usuario.setTelefone(request.getTelefone());
        usuario.setCep(request.getCep());
        usuario.setRua(request.getRua());
        usuario.setNumero(request.getNumero());
        usuario.setComplemento(request.getComplemento());
        usuario.setBairro(request.getBairro());
        usuario.setCidade(request.getCidade());
        usuario.setEstado(request.getEstado());

        Usuario saved = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(saved);
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getSenha())
            );
        } catch (AuthenticationException ex) {
            throw new CredenciaisInvalidasException(ApiMessages.CREDENCIAIS_INVALIDAS);
        }

        String token = jwtService.generateToken(request.getUsername());
        return new LoginResponse(token);
    }

    /**
     * Retorna dados do usuario autenticado no contexto atual.
     *
     * @return dados do usuario logado
     */
    public UsuarioResponse getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        if (username == null || username.isBlank() || "anonymousUser".equalsIgnoreCase(username)) {
            throw new CredenciaisInvalidasException(ApiMessages.CREDENCIAIS_INVALIDAS);
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ApiMessages.USUARIO_AUTENTICADO_NAO_ENCONTRADO));
        return UsuarioResponse.fromEntity(usuario);
    }
}