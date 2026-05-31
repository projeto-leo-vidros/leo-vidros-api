package com.project.extension.service;

import com.project.extension.controller.usuario.dto.UsuarioMapper;
import com.project.extension.entity.Endereco;
import com.project.extension.entity.Usuario;
import com.project.extension.exception.naoencontrado.UsuarioNaoEncontradoException;
import com.project.extension.repository.UsuarioRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final LogService logService;
    private final UsuarioMapper usuarioMapper;
    private final EnderecoService enderecoService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    @Transactional(rollbackFor = Exception.class)
    public Usuario salvar(Usuario usuario) {
            boolean isNovo = (usuario.getId() == null);
            
            if (usuario.getEndereco() != null) {
                Endereco endereco = enderecoService.cadastrar(usuario.getEndereco());
                usuario.setEndereco(endereco);
            }
            
            Usuario salvo = repository.save(usuario);

            String acao = isNovo ? "criado" : "salvo";
            String mensagem = String.format("Usuário ID %d %s com sucesso. E-mail: %s.",
                    salvo.getId(), acao, salvo.getEmail());
            logService.success(mensagem);
            return salvo;
    }

    public Usuario buscarPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Usuário com ID {} não encontrado", id);
            return new UsuarioNaoEncontradoException();
        });
    }

    public Page<Usuario> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletar(Integer id) {
        Usuario usuarioParaDeletar = this.buscarPorId(id);
        try {
            if (usuarioParaDeletar.getEndereco() != null && usuarioParaDeletar.getEndereco().getId() != null) {
                enderecoService.deletar(usuarioParaDeletar.getEndereco().getId());
            }
        } catch (Exception e) {
            logService.warning(String.format("Falha ao deletar endereço do Usuário ID %d: %s", id, e.getMessage()));
            log.warn("Erro ao deletar endereço durante deleção de usuário", e);
        }
        repository.deleteById(id);
    }

    public Usuario buscarPorEmail(@NotBlank String email) {
        return repository.findByEmail(email).orElseThrow(() -> {
            String mensagem = String.format("Falha na busca: Usuário com e-mail '%s' não encontrado.", email);
            logService.warning(mensagem);
            log.error(mensagem);
            return new UsuarioNaoEncontradoException();
        });
    }

    private void atualizarCampos(Usuario destino, Usuario origem) {
        destino.setNome(origem.getNome());
        destino.setCpf(origem.getCpf());
        destino.setEmail(origem.getEmail());
        destino.setTelefone(origem.getTelefone());

        if (origem.getSenha() != null && !origem.getSenha().isEmpty()) {
            destino.setSenha(passwordEncoder.encode(origem.getSenha()));
            logService.warning(String.format("Usuário ID %d: Senha alterada (apenas registro de ação).", destino.getId()));
        }

    }

    private Endereco atualizarEndereco(Endereco antigo, Endereco novo) {
        if (antigo == null && novo != null) {
            return enderecoService.cadastrar(novo);
        }

        if (novo == null) {
            return antigo;
        }

        enderecoService.editar(novo, antigo.getId());
        return enderecoService.buscarPorId(antigo.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public Usuario editar(Usuario origem, Integer id) {
        Usuario destino = this.buscarPorId(id);

        this.atualizarCampos(destino, origem);
        destino.setEndereco(this.atualizarEndereco(destino.getEndereco(), origem.getEndereco()));

        return repository.save(destino);
    }

    public String encodePassword(String senha) {
        return passwordEncoder.encode(senha);
    }

    public void definirSenhaInicial(Integer idUsuario, String novaSenha) {
        Usuario usuario = buscarPorId(idUsuario);
        String senhaCriptografada = passwordEncoder.encode(novaSenha);
        usuario = usuarioMapper.updateSenha(usuario, senhaCriptografada);
        repository.save(usuario);

        String mensagem = String.format("Senha inicial definida com sucesso para o Usuário ID %d. 'First Login' marcado como FALSE.", idUsuario);
        logService.success(mensagem);
    }

    public void enviarSenhaTemporaria(String email) {
        Usuario usuario = this.buscarPorEmail(email);
        if (usuario != null) {
            String senhaTemporaria = this.gerarSenhaTemporaria();
            usuario.setSenha(this.encodePassword(senhaTemporaria));
            this.enviarEmailComSenha(usuario, senhaTemporaria);
            usuario.setFirstLogin(true);
            this.salvar(usuario);
        }
    }

    private String gerarSenhaTemporaria() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private void enviarEmailComSenha(Usuario usuario, String senha) {
        String conteudoHtml = emailService.gerarEmailSenhaTemporaria(usuario.getNome(), senha);
        emailService.enviarEmail(usuario.getEmail(), "Senha Temporária", conteudoHtml);
    }
}
