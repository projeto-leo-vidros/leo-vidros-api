package com.project.extension.config;

import com.project.extension.repository.PedidoRepository;
import com.project.extension.service.PedidoConclusaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final PedidoRepository pedidoRepository;
    private final PedidoConclusaoService pedidoConclusaoService;

    @Override
    public void run(ApplicationArguments args) {
        corrigirPedidosConcluidos();
    }

    private void corrigirPedidosConcluidos() {
        int reabertosInvalidos = pedidoConclusaoService.corrigirPedidosServicoComConclusaoInvalida();
        if (reabertosInvalidos > 0) {
            log.info("{} pedido(s) de serviço com conclusão inválida foram reabertos automaticamente.", reabertosInvalidos);
        }

        int porEtapa = pedidoRepository.finalizarPedidosConcluidos();
        if (porEtapa > 0) {
            log.info("{} pedido(s) com etapa CONCLUÍDO marcado(s) como INATIVO.", porEtapa);
        }

        int porAgendamento = pedidoRepository.finalizarPedidosComAgendamentoConcluido();
        if (porAgendamento > 0) {
            log.info("{} pedido(s) com agendamento de serviço CONCLUÍDO marcado(s) como INATIVO.", porAgendamento);
        }

        int reabertosAposFinalizacao = pedidoConclusaoService.corrigirPedidosServicoComConclusaoInvalida();
        if (reabertosAposFinalizacao > 0) {
            log.info("{} pedido(s) concluídos sem requisitos válidos foram reabertos após a conciliação inicial.", reabertosAposFinalizacao);
        }
    }
}
