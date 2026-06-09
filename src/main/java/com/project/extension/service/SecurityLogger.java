package com.project.extension.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SecurityLogger {

    public void logLoginAttempt(String email, String ip, boolean sucesso) {
        if (sucesso) {
            log.info("Login bem-sucedido — email: {}, IP: {}", email, ip);
        } else {
            log.warn("Falha de login — email: {}, IP: {}", email, ip);
        }
    }

    public void logUnauthorizedAccess(String email, String recurso, String ip) {
        log.warn("Acesso não autorizado — email: {}, recurso: {}, IP: {}", email, recurso, ip);
    }

    public void logDataAccess(String usuario, String entidade, Integer id) {
        log.debug("Acesso a dado — usuário: {}, entidade: {}, ID: {}", usuario, entidade, id);
    }

    public void logDataModification(String usuario, String entidade, String operacao) {
        log.info("Modificação de dado — usuário: {}, entidade: {}, operação: {}", usuario, entidade, operacao);
    }

    public void logSecurityEvent(String evento, String detalhes) {
        log.warn("Evento de segurança: {} — {}", evento, detalhes);
    }
}
