package com.project.extension.controller.auth;

import com.project.extension.controller.auth.dto.MeResponseDto;
import com.project.extension.entity.Usuario;
import com.project.extension.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Operações relacionadas a autenticação de usuários")
@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Dados do usuário autenticado", description = "Retorna os dados básicos do usuário autenticado via cookie JWT. Usado pelo frontend para validar sessão ativa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado"),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada")
    })
    public ResponseEntity<MeResponseDto> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        return ResponseEntity.ok(new MeResponseDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getFirstLogin()
        ));
    }
}
