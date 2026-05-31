package com.project.extension.service;

import com.project.extension.entity.RefreshToken;
import com.project.extension.entity.Usuario;
import com.project.extension.exception.RegraNegocioException;
import com.project.extension.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration:604800000}") // 7 dias em ms
    private long refreshExpiration;

    private final RefreshTokenRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public RefreshToken criar(Usuario usuario) {
        repository.deleteByUsuario(usuario);

        RefreshToken token = new RefreshToken(
                UUID.randomUUID().toString(),
                usuario,
                Instant.now().plusMillis(refreshExpiration)
        );
        return repository.save(token);
    }

    public RefreshToken validar(String token) {
        RefreshToken rt = repository.findByToken(token)
                .orElseThrow(() -> new RegraNegocioException("Refresh token inválido ou não encontrado."));

        if (rt.isExpirado()) {
            repository.delete(rt);
            throw new RegraNegocioException("Refresh token expirado. Faça login novamente.");
        }
        return rt;
    }

    @Transactional(rollbackFor = Exception.class)
    public void revogarPorUsuario(Usuario usuario) {
        repository.deleteByUsuario(usuario);
    }
}
