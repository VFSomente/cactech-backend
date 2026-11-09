package com.cactech.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envio de e-mails transacionais.
 * <p>
 * Se nenhum servidor SMTP estiver configurado (spring.mail.host), o conteudo e
 * apenas registrado no log — util em desenvolvimento.
 */
@Service
public class SmtpEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String remetente;

    public SmtpEmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                        @Value("${app.mail.from:CacTech <nao-responda@cactech.com.br>}") String remetente) {
        this.mailSenderProvider = mailSenderProvider;
        this.remetente = remetente;
    }

    @Override
    public void enviarLinkRedefinicaoSenha(String para, String nome, String link, long validadeMinutos) {
        String texto = """
                Olá, %s!

                Recebemos um pedido para redefinir a senha da sua conta CacTech.
                Para criar uma nova senha, acesse o link abaixo (válido por %d minutos):

                %s

                Se você não pediu a redefinição, ignore este e-mail. Sua senha continua a mesma.

                Equipe CacTech
                """.formatted(nome, validadeMinutos, link);

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("SMTP nao configurado. Link de redefinicao de senha para {}: {}", para, link);
            return;
        }

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(para);
        mensagem.setSubject("Redefinição de senha — CacTech");
        mensagem.setText(texto);
        try {
            mailSender.send(mensagem);
        } catch (MailException ex) {
            // Nao revela a falha ao cliente (evita descobrir quais e-mails existem); registra para a equipe.
            log.error("Falha ao enviar e-mail de redefinicao para {}", para, ex);
        }
    }
}
