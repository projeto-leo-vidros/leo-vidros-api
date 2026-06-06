package com.project.extension.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarEmail(String para, String assunto, String conteudoHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(conteudoHtml, true);

            mailSender.send(message);
            log.info("E-mail enviado com sucesso para {} — assunto: \"{}\".", para, assunto);
        } catch (MessagingException e) {
            log.error("Falha ao enviar e-mail para {} — assunto: \"{}\".", para, assunto, e);
            throw new RuntimeException("Falha ao enviar email", e);
        }
    }

    public String gerarEmailAceito(String nomeUsuario, String email, String senha) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'><title>Solicitação Aceita</title></head>" +
                "<body style='margin:0; padding:0; background-color:#e6f2ff; font-family: Arial, sans-serif;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0' style='padding:30px 0;'>" +
                "<tr>" +
                "<td align='center'>" +
                "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; padding:20px; border-radius:12px; box-shadow:0 8px 16px rgba(0,0,0,0.1);'>" +
                "<tr><td align='center'>" +
                "<h1 style='color:#007acc; margin-bottom:20px;'>Bem-vindo à Léo Vidros!</h1>" +
                "<p style='color:#004080; font-size:16px; margin:0 0 15px 0;'>Olá <b>" + nomeUsuario + "</b>, sua solicitação foi aprovada.</p>" +
                "<p style='color:#004080; font-size:16px; margin:0 0 15px 0; font-weight:bold;'>Aqui estão suas credenciais:</p>" +
                "<div style='background-color:#cce6ff; padding:10px; border-radius:8px; margin-top:10px;'>" +
                "<p style='color:#004080; font-size:16px; margin:0;'>" +
                "<b>Email:</b> " + email + "<br>" +
                "<b>Senha temporária:</b> " + senha +
                "</p></div>" +
                "<p style='color:#004080; font-size:16px; margin-top:15px; font-weight:bold;'>No primeiro login, é obrigatório alterar sua senha.</p>" +
                "<p style='text-align:center; font-size:12px; color:#666; margin-top:20px;'>© 2025 Léo Vidros. Todos os direitos reservados.</p>" +
                "</td></tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</body>" +
                "</html>";
    }

    public String gerarEmailRecusado(String nomeUsuario) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'><title>Solicitação Recusada</title></head>" +
                "<body style='margin:0; padding:0; background-color:#e6f2ff; font-family: Arial, sans-serif;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0' style='padding:30px 0;'>" +
                "<tr>" +
                "<td align='center'>" +
                "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; padding:20px; border-radius:12px; box-shadow:0 8px 16px rgba(0,0,0,0.1);'>" +
                "<tr><td align='center'>" +
                "<h1 style='color:#007acc; margin-bottom:20px;'>Solicitação Recusada</h1>" +
                "<p style='color:#004080; font-size:16px; margin:0;'>Olá <b>" + nomeUsuario + "</b>, a Léo Vidros agradece, mas infelizmente seu cadastro não foi aceito.</p>" +
                "<p style='text-align:center; font-size:12px; color:#666; margin-top:20px;'>© 2025 Léo Vidros. Todos os direitos reservados.</p>" +
                "</td></tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</body>" +
                "</html>";
    }

    public String gerarEmailSenhaTemporaria(String nomeUsuario, String senhaTemporaria) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'><title>Senha Temporária</title></head>" +
                "<body style='margin:0; padding:0; background-color:#e6f2ff; font-family: Arial, sans-serif;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0' style='padding:30px 0;'>" +
                "<tr>" +
                "<td align='center'>" +
                "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; padding:20px; border-radius:12px; box-shadow:0 8px 16px rgba(0,0,0,0.1);'>" +
                "<tr><td align='center'>" +
                "<h1 style='color:#007acc; margin-bottom:20px;'>Senha Temporária de Acesso</h1>" +
                "<p style='color:#004080; font-size:16px; margin:0 0 15px 0;'>Olá <b>" + nomeUsuario + "</b>,</p>" +
                "<p style='color:#004080; font-size:16px; margin:0 0 20px 0;'>Esta é sua senha temporária para acessar o sistema da Léo Vidros.</p>" +
                "<div style='background-color:#cce6ff; padding:15px; border-radius:8px; margin:20px 0; border:1px solid #99ccff;'>" +
                "<p style='color:#004080; font-size:16px; margin:0;'>" +
                "<b>Senha temporária:</b> <span style='font-size:18px; font-family: monospace; letter-spacing:2px; color:#003366;'>" + senhaTemporaria + "</span>" +
                "</p></div>" +
                "<div style='background-color:#e6f2ff; padding:15px; border-radius:8px; margin:20px 0; border-left:4px solid #007acc;'>" +
                "<p style='color:#004080; font-size:15px; margin:0; line-height:1.6;'>" +
                "<b>Importante:</b> Por questões de segurança, você deve alterar esta senha no primeiro acesso ao sistema." +
                "</p></div>" +
                "<p style='color:#004080; font-size:16px; margin:15px 0;'>Faça login e siga as instruções para definir sua senha pessoal.</p>" +
                "<p style='text-align:center; font-size:12px; color:#666; margin-top:30px;'>© 2025 Léo Vidros. Todos os direitos reservados.</p>" +
                "</td></tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</body>" +
                "</html>";
    }
}