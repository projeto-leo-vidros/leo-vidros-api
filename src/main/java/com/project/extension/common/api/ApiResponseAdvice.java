package com.project.extension.common.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Hidden
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> paramType = returnType.getParameterType();
        if (ApiResponse.class.isAssignableFrom(paramType)) return false;
        if (SseEmitter.class.isAssignableFrom(paramType)) return false;
        if (Resource.class.isAssignableFrom(paramType)) return false;
        if (byte[].class.equals(paramType)) return false;
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof ApiResponse<?>) return body;
        if (body instanceof byte[]) return body;
        if (body instanceof Resource) return body;

        String path = request.getURI().getPath();
        if (isExcludedPath(path)) return body;

        if (selectedContentType != null && isBinaryOrStreamMediaType(selectedContentType)) {
            return body;
        }

        int statusCode = resolveStatusCode(response);
        // 204 / 304 não carregam corpo por definição — não envelopar.
        if (statusCode == 204 || statusCode == 304) return body;

        boolean isError = statusCode >= 400;

        if (body == null) {
            return isError
                    ? ApiResponse.error("HTTP_" + statusCode, messageForStatus(statusCode))
                    : ApiResponse.success(null);
        }

        if (body instanceof String s) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            try {
                ApiResponse<?> wrapped = isError
                        ? ApiResponse.error("HTTP_" + statusCode, s)
                        : ApiResponse.success(s);
                return objectMapper.writeValueAsString(wrapped);
            } catch (JsonProcessingException e) {
                log.error("Falha ao serializar resposta String em ApiResponse", e);
                return body;
            }
        }

        return isError
                ? ApiResponse.error("HTTP_" + statusCode, messageForStatus(statusCode), body)
                : ApiResponse.success(body);
    }

    private int resolveStatusCode(ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse servletResponse) {
            return servletResponse.getServletResponse().getStatus();
        }
        return 200;
    }

    private String messageForStatus(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Requisição inválida";
            case 401 -> "Não autorizado";
            case 403 -> "Acesso negado";
            case 404 -> "Recurso não encontrado";
            case 409 -> "Conflito";
            case 422 -> "Entidade não processável";
            case 429 -> "Muitas requisições";
            case 500 -> "Erro interno do servidor";
            default -> "Erro";
        };
    }

    private boolean isExcludedPath(String path) {
        return path.contains("/swagger-ui")
                || path.contains("/api-docs")
                || path.contains("/v3/api-docs")
                || path.contains("/orcamentos/stream")
                || path.contains("/actuator");
    }

    private boolean isBinaryOrStreamMediaType(MediaType type) {
        return MediaType.TEXT_EVENT_STREAM.includes(type)
                || MediaType.APPLICATION_OCTET_STREAM.includes(type)
                || MediaType.APPLICATION_PDF.includes(type)
                || type.getType().equals("image")
                || type.toString().contains("spreadsheetml")
                || type.toString().contains("officedocument");
    }
}
