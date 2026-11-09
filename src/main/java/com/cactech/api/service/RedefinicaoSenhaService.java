package com.cactech.api.service;

import com.cactech.api.constant.ApiMessages;
import com.cactech.api.model.TokenRedefinicaoSenha;
import com.cactech.api.model.Usuario;
import com.cactech.api.repository.TokenRedefinicaoSenhaRepository;
import com.cactech.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Fluxo de "esqueci a senha": gera um link de uso unico e redefine a senha a partir dele.
 */
@Service
public class RedefinicaoSenhaService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final TokenRedefinicaoSenhaRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String frontendUrl;
    private final long validadeMinutos;
    private final Clock clock;

    @Autowired
    public RedefinicaoSenhaService(UsuarioRepository usuarioRepository,
                                   TokenRedefinicaoSenhaRepository tokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   EmailService emailService,
                                   @Value("${app.frontend-url:http://localhost:4300}") String frontendUrl,
                                   @Value("${app.redefinicao-senha.validade-minutos:30}") long validadeMinutos) {
        this(usuarioRepository, tokenRepository, passwordEncoder, emailService, frontendUrl, validadeMinutos,
                Clock.systemDefaultZone());
    }

    RedefinicaoSenhaService(UsuarioRepository usuarioRepository,
                            TokenRedefinicaoSenhaRepository tokenRepository,
                            PasswordEncoder passwordEncoder,
                            EmailService emailService,
                            String frontendUrl,
                            long validadeMinutos,
                            Clock clock) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
        this.validadeMinutos = validadeMinutos;
        this.clock = clock;
    }

    /**
     * Gera e envia o link se o e-mail existir. Nao informa ao chamador se o e-mail
     * esta cadastrado, para nao permitir descobrir contas.
     */
    @Transactional
    public void solicitar(String email) {
        usuarioRepository.findByEmailIgnoreCase(email.trim()).ifPresent(usuario -> {
            LocalDateTime agora = LocalDateTime.now(clock);

            // Apenas o link mais recente vale
            tokenRepository.findByUsuarioAndUsadoEmIsNull(usuario).forEach(t -> t.setUsadoEm(agora));

            String token = gerarToken();
            TokenRedefinicaoSenha registro = new TokenRedefinicaoSenha();
            registro.setUsuario(usuario);
            registro.setTokenHash(hash(token));
            registro.setCriadoEm(agora);
            registro.setExpiraEm(agora.plusMinutes(validadeMinutos));
            tokenRepository.save(registro);

            String link = frontendUrl + "/redefinir-senha?token=" + token;
            String nome = usuario.getNomeCompleto() != null ? usuario.getNomeCompleto() : usuario.getUsername();
            emailService.enviarLinkRedefinicaoSenha(usuario.getEmail(), nome, link, validadeMinutos);
        });
    }

    @Transactional
    public void redefinir(String token, String novaSenha) {
        LocalDateTime agora = LocalDateTime.now(clock);
        TokenRedefinicaoSenha registro = tokenRepository.findByTokenHash(hash(token.trim()))
                .filter(t -> t.isValido(agora))
                .orElseThrow(() -> new IllegalArgumentException(ApiMessages.LINK_REDEFINICAO_INVALIDO));

        Usuario usuario = registro.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        registro.setUsadoEm(agora);
        tokenRepository.save(registro);
    }

    private static String gerarToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 indisponivel", ex);
        }
    }
}
