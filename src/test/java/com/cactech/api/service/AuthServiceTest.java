package com.cactech.api.service;

import com.cactech.api.dto.LoginRequest;
import com.cactech.api.dto.LoginResponse;
import com.cactech.api.dto.RegisterRequest;
import com.cactech.api.dto.UsuarioResponse;
import com.cactech.api.model.Usuario;
import com.cactech.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    void shouldRegisterUserWithEncryptedPassword() {
        JwtService jwtService = new JwtService("12345678901234567890123456789012", 3600000L);
        AuthService authService = new AuthService(usuarioRepository, passwordEncoder, jwtService, authenticationManager);

        RegisterRequest request = new RegisterRequest();
        request.setNomeCompleto("Usuario Teste");
        request.setUsername("usuarioteste");
        request.setSenha("123456");
        request.setEmail("user@test.com");
        request.setTelefone("11999999999");
        request.setCep("01001000");
        request.setRua("Rua A");
        request.setNumero("100");
        request.setComplemento("Apto 10");
        request.setBairro("Centro");
        request.setCidade("Sao Paulo");
        request.setEstado("SP");

        when(usuarioRepository.existsByEmail(eq("user@test.com"))).thenReturn(false);
        when(usuarioRepository.existsByUsername(eq("usuarioteste"))).thenReturn(false);
        when(passwordEncoder.encode(eq("123456"))).thenReturn("encoded-pass");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UsuarioResponse response = authService.register(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario savedUsuario = captor.getValue();

        assertEquals("encoded-pass", savedUsuario.getSenha());
        assertEquals(1L, response.id());
        assertEquals("usuarioteste", response.username());
    }

    @Test
    void shouldLoginAndReturnJwtToken() {
        JwtService jwtService = new JwtService("12345678901234567890123456789012", 3600000L);
        AuthService authService = new AuthService(usuarioRepository, passwordEncoder, jwtService, authenticationManager);

        LoginRequest request = new LoginRequest();
        request.setUsername("usuarioteste");
        request.setSenha("123456");

        LoginResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertNotNull(response);
        assertTrue(response.getToken() != null && !response.getToken().isBlank());
    }
}