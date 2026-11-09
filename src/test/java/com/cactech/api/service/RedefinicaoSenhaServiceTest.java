package com.cactech.api.service;

import com.cactech.api.model.TokenRedefinicaoSenha;
import com.cactech.api.model.Usuario;
import com.cactech.api.repository.TokenRedefinicaoSenhaRepository;
import com.cactech.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedefinicaoSenhaServiceTest {

    private static final ZoneId ZONA = ZoneId.of("America/Fortaleza");
    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-11-09T12:00:00Z"), ZONA);
    private static final LocalDateTime AGORA = LocalDateTime.now(RELOGIO);

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRedefinicaoSenhaRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    private RedefinicaoSenhaService service;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        service = new RedefinicaoSenhaService(usuarioRepository, tokenRepository, passwordEncoder, emailService,
                "http://localhost:4300/", 30, RELOGIO);
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("victor");
        usuario.setNomeCompleto("Victor Teste");
        usuario.setEmail("victor@example.com");
        usuario.setSenha("hash-antigo");
    }

    @Test
    void shouldSendLinkWithTokenAndStoreOnlyItsHash() {
        when(usuarioRepository.findByEmailIgnoreCase("victor@example.com")).thenReturn(Optional.of(usuario));
        when(tokenRepository.findByUsuarioAndUsadoEmIsNull(usuario)).thenReturn(List.of());

        service.solicitar(" victor@example.com ");

        ArgumentCaptor<String> link = ArgumentCaptor.forClass(String.class);
        verify(emailService).enviarLinkRedefinicaoSenha(eq("victor@example.com"), eq("Victor Teste"), link.capture(), eq(30L));
        assertTrue(link.getValue().startsWith("http://localhost:4300/redefinir-senha?token="));
        String token = link.getValue().substring(link.getValue().indexOf("token=") + 6);

        ArgumentCaptor<TokenRedefinicaoSenha> salvo = ArgumentCaptor.forClass(TokenRedefinicaoSenha.class);
        verify(tokenRepository).save(salvo.capture());
        assertEquals(RedefinicaoSenhaService.hash(token), salvo.getValue().getTokenHash());
        assertEquals(AGORA.plusMinutes(30), salvo.getValue().getExpiraEm());
    }

    @Test
    void shouldDoNothingForUnknownEmail() {
        when(usuarioRepository.findByEmailIgnoreCase("ninguem@example.com")).thenReturn(Optional.empty());

        service.solicitar("ninguem@example.com");

        verify(emailService, never()).enviarLinkRedefinicaoSenha(anyString(), anyString(), anyString(), anyLong());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void shouldResetPasswordAndConsumeToken() {
        TokenRedefinicaoSenha registro = token(AGORA.plusMinutes(10), null);
        when(tokenRepository.findByTokenHash(RedefinicaoSenhaService.hash("abc"))).thenReturn(Optional.of(registro));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("hash-novo");

        service.redefinir("abc", "novaSenha123");

        assertEquals("hash-novo", usuario.getSenha());
        assertNotNull(registro.getUsadoEm());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void shouldRejectExpiredToken() {
        when(tokenRepository.findByTokenHash(RedefinicaoSenhaService.hash("abc")))
                .thenReturn(Optional.of(token(AGORA.minusMinutes(1), null)));

        assertThrows(IllegalArgumentException.class, () -> service.redefinir("abc", "novaSenha123"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void shouldRejectAlreadyUsedToken() {
        when(tokenRepository.findByTokenHash(RedefinicaoSenhaService.hash("abc")))
                .thenReturn(Optional.of(token(AGORA.plusMinutes(10), AGORA.minusMinutes(5))));

        assertThrows(IllegalArgumentException.class, () -> service.redefinir("abc", "novaSenha123"));
    }

    private TokenRedefinicaoSenha token(LocalDateTime expiraEm, LocalDateTime usadoEm) {
        TokenRedefinicaoSenha registro = new TokenRedefinicaoSenha();
        registro.setUsuario(usuario);
        registro.setTokenHash("x");
        registro.setCriadoEm(AGORA.minusMinutes(20));
        registro.setExpiraEm(expiraEm);
        registro.setUsadoEm(usadoEm);
        return registro;
    }
}
