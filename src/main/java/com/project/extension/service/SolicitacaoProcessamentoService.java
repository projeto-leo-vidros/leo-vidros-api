package com.project.extension.service;

import com.project.extension.entity.Solicitacao;
import com.project.extension.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Bean dedicado ao processamento assíncrono dos desfechos de uma solicitação de acesso
 * (aceite → cria usuário + envia credenciais; recusa → envia e-mail de recusa).
 * <p>
 * Fica separado do {@link SolicitacaoService} de propósito: o {@code @Async} do Spring funciona
 * via proxy e é ignorado em auto-invocação (chamada {@code this.metodo()} dentro da mesma classe).
 * Como o {@code SolicitacaoService} chamava esses fluxos internamente, o envio de e-mail (SMTP)
 * rodava de forma síncrona e travava a resposta HTTP. Em um bean próprio a chamada passa pelo
 * proxy e o processamento ocorre de fato em background.
 */
@Slf4j
@Service
@AllArgsConstructor
public class SolicitacaoProcessamentoService {

    private final UsuarioService usuarioService;
    private final EmailService emailService;
    private final LogService logService;

    @Async
    public void criarUsuarioEEnviarEmail(Solicitacao solicitacao) {
        try {
            String senhaTemporaria = gerarSenhaTemporaria();
            log.debug("Senha temporária gerada: {}", senhaTemporaria);

            String senhaCriptografada = usuarioService.encodePassword(senhaTemporaria);

            Usuario usuario = new Usuario(
                    solicitacao.getNome(),
                    solicitacao.getEmail(),
                    solicitacao.getCpf(),
                    senhaCriptografada,
                    solicitacao.getTelefone(),
                    true
            );

            usuarioService.salvar(usuario);
            logService.success(String.format("Novo Usuário ID %d criado a partir da Solicitacao ID %d. E-mail: %s.",
                    usuario.getId(), solicitacao.getId(), usuario.getEmail()));

            enviarEmailAceite(usuario.getNome(), usuario.getEmail(), senhaTemporaria);
        } catch (Exception e) {
            logService.fatal(String.format("Erro FATAL ao criar usuário e enviar e-mail para Solicitacao ID %d.",
                    solicitacao.getId()), e);
            log.error("Erro ao criar usuário ou enviar email: {}", e.getMessage());
        }
    }

    @Async
    public void enviarEmailRecusa(String nomeUsuario, String email) {
        try {
            String conteudoHtml = emailService.gerarEmailRecusado(nomeUsuario);
            emailService.enviarEmail(email, "Solicitação Recusada", conteudoHtml);
            logService.info(String.format("Email de RECUSA enviado para: %s.", email));
        } catch (Exception e) {
            logService.fatal(String.format("Erro FATAL ao enviar e-mail de recusa para: %s.", email), e);
            log.error("Erro ao enviar email de recusa: {}", e.getMessage());
        }
    }

    private String gerarSenhaTemporaria() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private void enviarEmailAceite(String nomeUsuario, String email, String senha) {
        String conteudoHtml = emailService.gerarEmailAceito(nomeUsuario, email, senha);
        emailService.enviarEmail(email, "Solicitação Aceita", conteudoHtml);
        logService.info(String.format("Email de ACEITE com credenciais enviado para: %s.", email));
    }
}
