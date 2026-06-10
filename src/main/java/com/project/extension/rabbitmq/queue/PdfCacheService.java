package com.project.extension.rabbitmq.queue;

import com.project.extension.service.OrcamentoSseService;
import com.project.extension.strategy.pdf.PdfStorageContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfCacheService {

    private final PdfStorageContext storageContext;
    private final OrcamentoSseService sseService;

    @RabbitListener(queues = "#{@orcamentoResponseQueue.name}")
    public void receberPdf(PdfResponse response) {
        if (response == null) return;

        storageContext.armazenar(response);

        notificarFrontend(response);
    }

    public byte[] obterPorNumeroOrcamento(String numeroOrcamento) {
        return storageContext.obterPorNumeroOrcamento(numeroOrcamento);
    }

    private void notificarFrontend(PdfResponse response) {
        try {
            Integer orcamentoId = response.getOrcamentoId();
            String numero = response.getNumeroOrcamento();

            if (orcamentoId != null && orcamentoId > 0) {
                sseService.enviarEvento(orcamentoId, "FINALIZADO");
            } else if (numero != null && !numero.isBlank()) {
                sseService.enviarEventoPorChave(numero, "FINALIZADO");
            } else {
                log.warn("Notificação SSE ignorada: response sem orcamentoId válido e sem numeroOrcamento válido");
            }
        } catch (Exception e) {
            log.error("Falha ao notificar frontend via SSE", e);
        }
    }
}
