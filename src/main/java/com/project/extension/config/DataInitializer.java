package com.project.extension.config;

import com.project.extension.entity.Categoria;
import com.project.extension.entity.Etapa;
import com.project.extension.entity.Status;
import com.project.extension.repository.CategoriaRepository;
import com.project.extension.repository.EtapaRepository;
import com.project.extension.repository.PedidoRepository;
import com.project.extension.repository.StatusRepository;
import com.project.extension.service.PedidoConclusaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final CategoriaRepository categoriaRepository;
    private final EtapaRepository etapaRepository;
    private final StatusRepository statusRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoConclusaoService pedidoConclusaoService;

    @Override
    public void run(ApplicationArguments args) {
        seedCategorias();
        seedEtapas();
        seedStatus();
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

    private void seedCategorias() {
        List<String> categorias = List.of("INFO", "ERROR", "DEBUG", "WARNING", "SUCCESS", "FATAL");
        for (String nome : categorias) {
            if (categoriaRepository.findFirstByNome(nome).isEmpty()) {
                categoriaRepository.save(new Categoria(nome));
                log.info("Categoria inserida: {}", nome);
            }
        }
    }

    private void seedEtapas() {
        List<String[]> etapas = List.of(
                new String[]{"PEDIDO", "AGUARDANDO AGENDA DE ORÇAMENTO"},
                new String[]{"PEDIDO", "ORÇAMENTO AGENDADO"},
                new String[]{"PEDIDO", "ANÁLISE DO ORÇAMENTO"},
                new String[]{"PEDIDO", "ORÇAMENTO APROVADO"},
                new String[]{"PEDIDO", "AGUARDANDO AGENDA DE SERVIÇO/INSTALAÇÃO"},
                new String[]{"PEDIDO", "SERVIÇO AGENDADO"},
                new String[]{"PEDIDO", "AGENDAMENTO EM EXECUÇÃO"},
                new String[]{"PEDIDO", "CONCLUÍDO"}
        );

        for (String[] e : etapas) {
            if (etapaRepository.findFirstByTipoAndNome(e[0], e[1]).isEmpty()) {
                etapaRepository.save(new Etapa(e[0], e[1]));
                log.info("Etapa inserida: {} - {}", e[0], e[1]);
            }
        }
    }

    private void seedStatus() {
        List<String[]> statuses = List.of(
                new String[]{"AGENDAMENTO", "PENDENTE"},
                new String[]{"AGENDAMENTO", "EM ANDAMENTO"},
                new String[]{"AGENDAMENTO", "CONCLUÍDO"},
                new String[]{"AGENDAMENTO", "CANCELADO"},

                new String[]{"PEDIDO", "ATIVO"},
                new String[]{"PEDIDO", "INATIVO"},
                new String[]{"PEDIDO", "CANCELADO"},

                new String[]{"SOLICITACAO", "PENDENTE"},
                new String[]{"SOLICITACAO", "ACEITO"},
                new String[]{"SOLICITACAO", "RECUSADO"},

                new String[]{"ORCAMENTO", "RASCUNHO"},
                new String[]{"ORCAMENTO", "ENVIADO"},
                new String[]{"ORCAMENTO", "EM ANALISE"},
                new String[]{"ORCAMENTO", "APROVADO"},
                new String[]{"ORCAMENTO", "RECUSADO"},
                new String[]{"ORCAMENTO", "EXPIRADO"}
        );

        for (String[] s : statuses) {
            if (statusRepository.findFirstByTipoAndNome(s[0], s[1]).isEmpty()) {
                statusRepository.save(new Status(s[0], s[1]));
                log.info("Status inserido: {} - {}", s[0], s[1]);
            }
        }
    }
}
