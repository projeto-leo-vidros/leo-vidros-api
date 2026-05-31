package com.project.extension.controller.auth;

import com.project.extension.controller.auth.dto.AuthRequestDto;
import com.project.extension.controller.auth.dto.AuthResponseDto;
import com.project.extension.controller.auth.dto.EsqueceuSenhaRquestDto;
import com.project.extension.entity.RefreshToken;
import com.project.extension.entity.Usuario;
import com.project.extension.service.LoginAttemptService;
import com.project.extension.service.RefreshTokenService;
import com.project.extension.service.SecurityLogger;
import com.project.extension.service.UsuarioService;
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
    private final RefreshTokenService refreshTokenService;
    
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

            RefreshToken refreshToken = refreshTokenService.criar(usuario);
            String refreshCookieFlags = isProduction && isSecureRequest
                    ? "refreshToken=%s; HttpOnly; Secure; SameSite=Strict; Path=/api/auth/refresh; Max-Age=%d"
                    : "refreshToken=%s; HttpOnly; SameSite=Strict; Path=/api/auth/refresh; Max-Age=%d";
            httpResponse.addHeader("Set-Cookie",
                    String.format(refreshCookieFlags, refreshToken.getToken(), 7 * 24 * 60 * 60));

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
        boolean secure = isProduction && isHttpsRequest(httpRequest);

        String clearAuth = secure
                ? "authToken=; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=0"
                : "authToken=; HttpOnly; SameSite=Strict; Path=/; Max-Age=0";
        String clearRefresh = secure
                ? "refreshToken=; HttpOnly; Secure; SameSite=Strict; Path=/api/auth/refresh; Max-Age=0"
                : "refreshToken=; HttpOnly; SameSite=Strict; Path=/api/auth/refresh; Max-Age=0";

        httpResponse.setHeader("Set-Cookie", clearAuth);
        httpResponse.addHeader("Set-Cookie", clearRefresh);

        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
                refreshTokenService.revogarPorUsuario(usuario);
            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok("Logout realizado com sucesso");
    }

    @Override
    public ResponseEntity<AuthResponseDto> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshTokenValue,
            HttpServletResponse httpResponse) {

        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        RefreshToken rt = refreshTokenService.validar(refreshTokenValue);
        Usuario usuario = rt.getUsuario();

        UserDetails userDetails = new User(usuario.getEmail(), usuario.getSenha(), java.util.List.of());
        String novoToken = tokenProvider.gerarToken(userDetails);

        boolean isProduction = "production".equals(environment);
        String cookieFlags = isProduction
                ? "authToken=%s; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=%d"
                : "authToken=%s; HttpOnly; SameSite=Strict; Path=/; Max-Age=%d";
        httpResponse.setHeader("Set-Cookie", String.format(cookieFlags, novoToken, 24 * 60 * 60));

        return ResponseEntity.ok(new AuthResponseDto(novoToken, usuario.getNome(), usuario.getId(), usuario.getFirstLogin(), usuario.getEmail()));
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