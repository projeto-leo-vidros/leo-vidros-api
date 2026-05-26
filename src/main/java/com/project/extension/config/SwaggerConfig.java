package com.project.extension.config;

import com.project.extension.common.api.ApiResponse;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestão da Empresa Léo Vidros")
                        .version("1.0.0")
                        .description("Esta API permite agendar, gerenciar serviços, orçamentos e estoque de produtos. "
                                + "Todas as respostas seguem o envelope { success, data, error }.")
                        .contact(new Contact()
                                .name("Equipe Léo Vidros")
                                .email("contato@leovidros.com.br")
                                .url("https://www.leovidros.com.br"))
                        .license(new License()
                                .name("Licença MIT")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public OperationCustomizer envelopeApiResponses() {
        return (operation, handlerMethod) -> {
            if (returnsApiResponseDirectly(handlerMethod)) return operation;
            if (operation.getResponses() == null) return operation;
            operation.getResponses().forEach((statusCode, apiResponse) -> {
                boolean isSuccess = statusCode != null && statusCode.startsWith("2");
                io.swagger.v3.oas.models.media.Content content = apiResponse.getContent();
                if (content == null || content.isEmpty()) {
                    io.swagger.v3.oas.models.media.Content newContent = new io.swagger.v3.oas.models.media.Content();
                    io.swagger.v3.oas.models.media.MediaType mt = new io.swagger.v3.oas.models.media.MediaType();
                    mt.setSchema(wrap(null, isSuccess));
                    newContent.addMediaType("application/json", mt);
                    apiResponse.setContent(newContent);
                    return;
                }
                content.forEach((mediaType, mediaTypeObj) -> {
                    if (!"application/json".equalsIgnoreCase(mediaType)) return;
                    Schema<?> existing = mediaTypeObj.getSchema();
                    mediaTypeObj.setSchema(wrap(existing, isSuccess));
                });
            });
            return operation;
        };
    }

    private boolean returnsApiResponseDirectly(HandlerMethod handlerMethod) {
        if (handlerMethod == null) return false;
        return isApiResponseType(handlerMethod.getMethod().getGenericReturnType());
    }

    private boolean isApiResponseType(Type type) {
        if (type instanceof Class<?> cls) {
            return ApiResponse.class.isAssignableFrom(cls);
        }
        if (type instanceof ParameterizedType pt && pt.getRawType() instanceof Class<?> rawCls) {
            if (ApiResponse.class.isAssignableFrom(rawCls)) return true;
            if (ResponseEntity.class.isAssignableFrom(rawCls)) {
                Type[] args = pt.getActualTypeArguments();
                return args.length > 0 && isApiResponseType(args[0]);
            }
        }
        return false;
    }

    @SuppressWarnings({"rawtypes"})
    private Schema<?> wrap(Schema<?> inner, boolean isSuccess) {
        ObjectSchema envelope = new ObjectSchema();
        envelope.addProperty("success", new BooleanSchema()._default(isSuccess));
        if (isSuccess) {
            Schema dataSchema = inner != null ? inner : new ObjectSchema().nullable(true);
            envelope.addProperty("data", dataSchema);
        } else {
            ObjectSchema error = new ObjectSchema();
            error.addProperty("code", new StringSchema().example("HTTP_400"));
            error.addProperty("message", new StringSchema().example("Requisição inválida"));
            error.addProperty("details", new ObjectSchema().nullable(true));
            envelope.addProperty("error", error);
        }
        return envelope;
    }

}
