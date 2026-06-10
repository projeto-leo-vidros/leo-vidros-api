package com.project.extension.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "fila.orcamento.pdf";
    public static final String DLQ_NAME = "fila.orcamento.pdf.falha";
    public static final String EXCHANGE_NAME = "exchange.leovidros.direct";
    public static final String DLX_NAME = "exchange.leovidros.dlx";
    // Fanout exchange publicado pelo microserviço de orçamento — todas as instâncias recebem o evento.
    public static final String RESPONSE_FANOUT_EXCHANGE = "exchange.leovidros.orcamento.resposta.fanout";
    public static final String ROUTING_KEY = "orcamento.gerar";
    public static final String DEAD_LETTER_ROUTING_KEY = "orcamento.falha";

    @Bean
    public Queue orcamentoQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .deadLetterExchange(DLX_NAME)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    // Fila anônima por instância: auto-delete, não durável, nome gerado pelo broker.
    // Com 2 app-servers, cada instância recebe uma cópia da resposta via fanout,
    // garantindo que o SSE emitter (in-memory) sempre seja encontrado.
    @Bean
    public Queue orcamentoResponseQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Queue orcamentoDeadLetterQueue() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    @Bean
    public DirectExchange leoVidrosExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange leoVidrosDeadLetterExchange() {
        return new DirectExchange(DLX_NAME);
    }

    @Bean
    public FanoutExchange orcamentoResponseFanoutExchange() {
        return new FanoutExchange(RESPONSE_FANOUT_EXCHANGE, true, false);
    }

    @Bean
    public Binding orcamentoBinding(Queue orcamentoQueue, DirectExchange leoVidrosExchange) {
        return BindingBuilder.bind(orcamentoQueue).to(leoVidrosExchange).with(ROUTING_KEY);
    }

    // Cada instância cria sua própria fila anônima vinculada ao fanout de respostas.
    @Bean
    public Binding orcamentoResponseBinding(Queue orcamentoResponseQueue,
                                            FanoutExchange orcamentoResponseFanoutExchange) {
        return BindingBuilder.bind(orcamentoResponseQueue).to(orcamentoResponseFanoutExchange);
    }

    @Bean
    public Binding orcamentoDeadLetterBinding(Queue orcamentoDeadLetterQueue,
                                              DirectExchange leoVidrosDeadLetterExchange) {
        return BindingBuilder.bind(orcamentoDeadLetterQueue)
                .to(leoVidrosDeadLetterExchange)
                .with(DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
