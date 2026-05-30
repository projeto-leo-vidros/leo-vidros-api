package com.project.extension.service;

import com.project.extension.controller.usuario.dto.UsuarioMapper;
import com.project.extension.entity.Solicitacao;
import com.project.extension.entity.Status;
import com.project.extension.entity.Usuario;
import com.project.extension.exception.RegraNegocioException;
import com.project.extension.repository.SolicitacaoRepository;
import com.project.extension.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class SolicitacaoService {
    private final SolicitacaoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final EmailService emailService;
    private final UsuarioMapper usuarioMapper;
    private final StatusService statusService;
    private final LogService logService;

    public Solicitacao cadastrar(Solicitacao solicitacao) {
        String emailNormalizado = normalizarEmail(solicitacao.getEmail());
        validarEmailDisponivel(emailNormalizado);

        solicitacao.setEmail(emailNormalizado);
        Status status = statusService.buscarPorTipoAndStatus("SOLICITACAO", "PENDENTE");
        solicitacao.setStatus(status);
        Solicitacao salvo = repository.save(solicitacao);

        String mensagem = String.format("Nova Solicitacao ID %d criada. Nome: %s, E-mail: %s. Status: PENDENTE.",
                salvo.getId(), salvo.getNome(), salvo.getEmail());
        logService.success(mensagem);
        return salvo;
    }

    private void validarEmailDisponivel(String email) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new RegraNegocioException("Este e-mail já está cadastrado no sistema.");
        }

        if (repository.existsByEmailIgnoreCaseAndStatusNomeIgnoreCase(email, "PENDENTE")) {
            throw new RegraNegocioException("Já existe uma solicitação pendente para este e-mail.");
        }
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public Page<Solicitacao> listarPorNome(String nome, Pageable pageable) {
        Page<Solicitacao> page = nome != null && !nome.isBlank()
                ? repository.findAllByNomeIgnoreCase(nome, pageable)
                : repository.findAll(pageable);
        logService.info(String.format("Busca por solicitação pelo nome '%s' realizada. Total: %d.", nome, page.getTotalElements()));
        return page;
    }

    public Page<Solicitacao> listar(String status, Pageable pageable) {
        return status != null && !status.isBlank()
                ? repository.findAllByStatusNomeIgnoreCase(status, pageable)
                : repository.findAll(pageable);
    }

    public void aceitarSolicitacao(Integer id) {
        repository.findById(id).ifPresentOrElse(solicitacao -> {
            Status aprovado = statusService.buscarPorTipoAndStatus("SOLICITACAO", "ACEITO");
            solicitacao.setStatus(aprovado);
            repository.save(solicitacao);

            logService.info(String.format("Solicitacao ID %d aceita. Status alterado para ACEITO.", id));

            try {
                criarUsuarioEEnviarEmail(solicitacao);
            } catch (Exception e) {
                logService.fatal(String.format("Erro FATAL ao criar usuário e enviar e-mail para Solicitacao ID %d.", id), e);
                log.error("Erro ao criar usuário ou enviar email: {}", e.getMessage());
            }
        }, () -> {
            logService.error(String.format("Tentativa de aceitar Solicitacao ID %d falhou: não encontrada.", id));
            log.warn("Solicitação não encontrada: id={}", id);
        });
    }

    public void recusarSolicitacao(Integer id) {
        repository.findById(id).ifPresentOrElse(solicitacao -> {
            Status recusado = statusService.buscarPorTipoAndStatus("SOLICITACAO", "RECUSADO");
            solicitacao.setStatus(recusado);
            repository.save(solicitacao);

            logService.warning(String.format("Solicitacao ID %d recusada. Status alterado para RECUSADO.", id));

            enviarEmailRecusa(solicitacao.getNome(), solicitacao.getEmail());
        }, () -> {
            logService.error(String.format("Tentativa de recusar Solicitacao ID %d falhou: não encontrada.", id));
        });
    }

    @Async
    void criarUsuarioEEnviarEmail(Solicitacao solicitacao) {
        String senhaTemporaria = gerarSenhaTemporaria();

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
    }

    private String gerarSenhaTemporaria() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private void enviarEmailAceite(String nomeUsuario, String email, String senha) {
        String conteudoHtml = emailService.gerarEmailAceito(nomeUsuario, email, senha);
        emailService.enviarEmail(email, "Solicitação Aceita", conteudoHtml);
        logService.info(String.format("Email de ACEITE com credenciais enviado para: %s.", email));
    }

    private void enviarEmailRecusa(String nomeUsuario, String email) {
        String conteudoHtml = emailService.gerarEmailRecusado(nomeUsuario);
        emailService.enviarEmail(email, "Solicitação Recusada", conteudoHtml);
        logService.info(String.format("Email de RECUSA enviado para: %s.", email));
    }
}
