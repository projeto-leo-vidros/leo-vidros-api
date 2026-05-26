package com.project.extension.controller.auth;

import com.project.extension.controller.auth.dto.AuthRequestDto;
import com.project.extension.controller.auth.dto.AuthResponseDto;
import com.project.extension.controller.auth.dto.EsqueceuSenhaRquestDto;
import com.project.extension.entity.Usuario;
import com.project.extension.service.UsuarioService;
import com.project.extension.service.LoginAttemptService;
import com.project.extension.service.SecurityLogger;
import com.project.extension.config.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthControllerDoc {

    private final AuthenticationManager authManager;
    private final TokenProvider tokenProvider;
    private final UsuarioService usuarioService;
    private final LoginAttemptService loginAttemptService;
    private final SecurityLogger securityLogger;
    
    @Value("${app.environment:development}")
    private String environment;

    @Override
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String clientIP = getClientIP(httpRequest);

        if (loginAttemptService.isBlocked(request.email())) {
            securityLogger.logUnauthorizedAccess(request.email(), "LOGIN_BLOCKED", clientIP);
            return ResponseEntity.status(429).build();
        }

        try {
            autenticar(request);
            
            Usuario usuario = usuarioService.buscarPorEmail(request.email());
            String token = gerarToken(usuario);

            boolean isProduction = "production".equals(environment);
            boolean isSecureRequest = isHttpsRequest(httpRequest);

            if (isProduction && isSecureRequest) {
                httpResponse.setHeader("Set-Cookie",
                    String.format("authToken=%s; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=%d",
                        token, 24 * 60 * 60));
            } else {
                httpResponse.setHeader("Set-Cookie",
                    String.format("authToken=%s; HttpOnly; SameSite=Strict; Path=/; Max-Age=%d",
                        token, 24 * 60 * 60));
            }

            loginAttemptService.loginSucceeded(request.email());
            securityLogger.logLoginAttempt(request.email(), clientIP, true);

            return ResponseEntity.ok(new AuthResponseDto(token, usuario.getNome(), usuario.getId(), usuario.getFirstLogin(), usuario.getEmail()));
            
        } catch (Exception e) {
            loginAttemptService.loginFailed(request.email());
            securityLogger.logLoginAttempt(request.email(), clientIP, false);
            throw e;
        }
    }

    @Override
    public ResponseEntity<String> esqueceuSenha(EsqueceuSenhaRquestDto dto) {
        usuarioService.enviarSenhaTemporaria(dto.email());
        return ResponseEntity.status(200).body("Email enviado com sucesso!");
    }
    
    @Override
    public ResponseEntity<String> logout(HttpServletResponse httpResponse, HttpServletRequest httpRequest) {
        boolean isProduction = "production".equals(environment);

        if (isProduction && isHttpsRequest(httpRequest)) {
            httpResponse.setHeader("Set-Cookie",
                "authToken=; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=0");
        } else {
            httpResponse.setHeader("Set-Cookie",
                "authToken=; HttpOnly; SameSite=Strict; Path=/; Max-Age=0");
        }
        
        return ResponseEntity.ok("Logout realizado com sucesso");
    }

    private void autenticar(AuthRequestDto request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email(),
                request.senha()
        ));
    }

    private String gerarToken(Usuario usuario) {
        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getSenha(),
                List.of()
        );
        return tokenProvider.gerarToken(userDetails);
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private boolean isHttpsRequest(HttpServletRequest request) {
        if (request.isSecure()) {
            return true;
        }
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        return forwardedProto != null && forwardedProto.equalsIgnoreCase("https");
    }
}