package com.project.extension.service;

import com.project.extension.entity.Usuario;
import com.project.extension.exception.naoencontrado.UsuarioNaoEncontradoException;
import com.project.extension.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final UsuarioRepository repository;
    private final LogService logService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> {
                    String mensagem = String.format("Tentativa de login falhou. Usuário com e-mail '%s' não encontrado no sistema.", email);
                    logService.warning(mensagem);
                    log.warn("Usuário não encontrado para o e-mail: {}", email);
                    return new UsuarioNaoEncontradoException();
                });

        String mensagemSucesso = String.format("Login bem-sucedido. Usuário ID %d autenticado com e-mail: %s.",
                usuario.getId(),
                email);
        logService.success(mensagemSucesso);

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                List.of()
        );
    }
}