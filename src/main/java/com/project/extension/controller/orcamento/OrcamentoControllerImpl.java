package com.project.extension.controller.orcamento;

import com.project.extension.controller.orcamento.dto.AtualizarStatusRequestDto;
import com.project.extension.controller.orcamento.dto.OrcamentoMapper;
import com.project.extension.controller.orcamento.dto.OrcamentoRequestDto;
import com.project.extension.controller.orcamento.dto.OrcamentoResponseDto;
import com.project.extension.controller.orcamento.dto.ReportDto;
import com.project.extension.entity.Orcamento;
import com.project.extension.rabbitmq.queue.PdfCacheService;
import com.project.extension.service.OrcamentoService;
import com.project.extension.service.OrcamentoSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orcamentos")
@RequiredArgsConstructor
public class OrcamentoControllerImpl implements OrcamentoControllerDoc {

    private final OrcamentoService service;
    private final OrcamentoMapper mapper;
    private final OrcamentoSseService sseService;
    private final PdfCacheService pdfCacheService;

    @Override
    public ResponseEntity<OrcamentoResponseDto> criar(OrcamentoRequestDto request) {
        Orcamento salvo = service.criar(request);
        return ResponseEntity.status(201).body(mapper.toResponse(salvo));
    }

    @Override
    public ResponseEntity<OrcamentoResponseDto> gerarPdf(Integer id) {
        Orcamento atualizado = service.gerarPdf(id);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @Override
    public ResponseEntity<OrcamentoResponseDto> buscarPorId(Integer id) {
        Orcamento orcamento = service.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(orcamento));
    }

    @Override
    public ResponseEntity<Page<OrcamentoResponseDto>> listar(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable).map(mapper::toResponse));
    }

    @Override
    public ResponseEntity<Page<OrcamentoResponseDto>> listarPorPedido(
            Integer pedidoId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorPedido(pedidoId, pageable).map(mapper::toResponse));
    }

    @Override
    public ResponseEntity<OrcamentoResponseDto> atualizar(Integer id, OrcamentoRequestDto request) {
        Orcamento atualizado = service.atualizar(id, request);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @Override
    public ResponseEntity<Void> deletar(Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<OrcamentoResponseDto> atualizarStatus(Integer id, AtualizarStatusRequestDto body) {
        String statusNome = body.status() != null ? body.status() : "ENVIADO";
        Orcamento atualizado = service.atualizarStatus(id, statusNome, body.pdfPath());
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @Override
    public ResponseEntity<?> baixarPdf(Integer id) {
        Orcamento orcamento = service.buscarPorId(id);
        String numeroOrcamento = orcamento.getNumeroOrcamento();
        byte[] pdf = numeroOrcamento != null
                ? pdfCacheService.obterPorNumeroOrcamento(numeroOrcamento)
                : null;

        if (pdf == null) {
            log.warn("[PDF] Cache miss — id={} numero='{}'. Acionando re-geração.", id, numeroOrcamento);
            service.gerarPdf(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of(
                            "mensagem", "PDF não encontrado no cache. Re-geração iniciada.",
                            "orcamentoId", id
                    ));
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"orcamento_" + numeroOrcamento + ".pdf\"")
                .body(pdf);
    }

    @Override
    public ResponseEntity<?> obterPdfPorNumero(@PathVariable String numeroOrcamento) {
        byte[] pdf = pdfCacheService.obterPorNumeroOrcamento(numeroOrcamento);

        if (pdf == null) {
            var orcamento = service.buscarPorNumeroOrcamento(numeroOrcamento);
            // Número inexistente → 404 (antes retornava 202 mesmo sem orçamento, induzindo o cliente ao erro).
            if (orcamento.isEmpty()) {
                log.warn("[PDF] Número de orçamento inexistente — numero='{}'.", numeroOrcamento);
                return ResponseEntity.notFound().build();
            }
            log.warn("[PDF] Cache miss por número — numero='{}' id={}. Acionando re-geração.", numeroOrcamento, orcamento.get().getId());
            service.gerarPdf(orcamento.get().getId());
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of("mensagem", "PDF não encontrado no cache. Re-geração iniciada."));
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"orcamento_" + numeroOrcamento + ".pdf\"")
                .body(pdf);
    }

    @Override
    public ResponseEntity<?> obterReportPorNumero(@PathVariable String numeroOrcamento) {
        byte[] pdf = pdfCacheService.obterPorNumeroOrcamento(numeroOrcamento);
        if (pdf == null) {
            return ResponseEntity.notFound().build();
        }

        ReportDto report = new ReportDto(
                pdf,
                MediaType.APPLICATION_PDF_VALUE,
                "orcamento_" + numeroOrcamento + ".pdf"
        );
        return ResponseEntity.ok(report);
    }

    @Override
    public ResponseEntity<?> verificarStatusPdfCache(@PathVariable String numeroOrcamento) {
        byte[] pdf = pdfCacheService.obterPorNumeroOrcamento(numeroOrcamento);

        boolean pronto = pdf != null;
        long tamanho = pronto ? pdf.length : 0;
        String mensagem = pronto ? "PDF pronto para download" : "PDF ainda não foi gerado";

        return ResponseEntity.ok(Map.of(
                "numeroOrcamento", numeroOrcamento,
                "pronto", pronto,
                "tamanho", tamanho,
                "mensagem", mensagem
        ));
    }

    @Override
    public SseEmitter streamProgresso(String orcamentoId) {
        return sseService.criarEmitter(orcamentoId);
    }

}
