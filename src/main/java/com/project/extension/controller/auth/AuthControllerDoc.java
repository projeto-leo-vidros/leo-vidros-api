package com.project.extension.controller.auth;

import com.project.extension.controller.auth.dto.AuthRequestDto;
import com.project.extension.controller.auth.dto.AuthResponseDto;
import com.project.extension.controller.auth.dto.EsqueceuSenhaRquestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Authentication", description = "Operações relacionadas a autenticação de usuários")
public interface AuthControllerDoc {

    @PostMapping("/login")
    @Operation(summary = "Autentificação de usuário", description = """
            Autentificação de usuário
            ---
            Autentificação de usuário no banco de dados
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Quando o usuário é autenticado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class)
                    )),
            @ApiResponse(responseCode = "401", description = "Quando o corpo de requisição está incorreto",
                    content = @Content())
    })
    ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthRequestDto request, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    @PostMapping("/forgot-password")
    @Operation(summary = "Esqueceu a senha", description = """
            Esqueceu a senha
            ---
            Endpoint para validar email e enviar uma senha temporária
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quando o usuário é autenticado com sucesso",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Quando o corpo de requisição está incorreto",
                    content = @Content()),
            @ApiResponse(responseCode = "404", description = "Quando o email não foi encontrado",
                    content = @Content())
    })
    ResponseEntity<String> esqueceuSenha(@RequestBody @Valid EsqueceuSenhaRquestDto dto);
    
    @PostMapping("/logout")
    @Operation(summary = "Logout do usuário", description = """
            Logout do usuário
            ---
            Endpoint para realizar logout e limpar o cookie de autenticação
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso",
                    content = @Content()),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado",
                    content = @Content())
    })
    ResponseEntity<String> logout(HttpServletResponse httpResponse, HttpServletRequest httpRequest);
}
