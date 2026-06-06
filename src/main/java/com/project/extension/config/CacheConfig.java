package com.project.extension.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * Cache distribuído com Redis para os endpoints de dashboard mais acessados
 * (faturamento mensal e anual). As entradas expiram por TTL e também são
 * invalidadas explicitamente quando um pedido é criado/editado/removido
 * (ver {@code @CacheEvict} em PedidoService).
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /** Cache de GET /dashboard/faturamento-mes. */
    public static final String FATURAMENTO_MES = "faturamentoMes";

    /** Cache de GET /dashboard/faturamento-anual. */
    public static final String FATURAMENTO_ANUAL = "faturamentoAnual";

    private static final Duration TTL_PADRAO = Duration.ofMinutes(5);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(TTL_PADRAO)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(Map.of(
                        FATURAMENTO_MES, base.entryTtl(Duration.ofMinutes(5)),
                        FATURAMENTO_ANUAL, base.entryTtl(Duration.ofMinutes(10))
                ))
                .build();
    }
}
