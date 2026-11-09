package com.cactech.api.service;

/**
 * Envio de e-mails transacionais da loja.
 */
public interface EmailService {

    void enviarLinkRedefinicaoSenha(String para, String nome, String link, long validadeMinutos);
}
