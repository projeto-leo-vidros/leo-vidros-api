package com.project.extension.controller.orcamento;

import com.project.extension.controller.orcamento.dto.*;
import com.project.extension.entity.Orcamento;
import com.project.extension.entity.StatusFila;
import com.project.extension.exception.naoencontrado.OrcamentoNaoEncontradoException;
import com.project.extension.rabbitmq.queue.PdfCacheService;
import com.project.extension.service.OrcamentoService;
import com.project.extension.service.OrcamentoSseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoControllerTests {

    @Mock private OrcamentoService service;
    @Mock private OrcamentoMapper mapper;
    @Mock private OrcamentoSseService sseService;
    @Mock private PdfCacheService pdfCacheService;

    @InjectMocks private OrcamentoControllerImpl controller;

    private Orcamento orcamento;
    private OrcamentoResponseDto responseDto;

    @BeforeEach
    void setUp() {
        orcamento = new Orcamento();
        orcamento.setId(1);
        orcamento.setNumeroOrcamento("ORC-001");
        orcamento.setStatusFila(StatusFila.PENDENTE);

        responseDto = new OrcamentoResponseDto(
                1, 10, 1, "Cliente Teste", "cliente@teste.com", "11999999999",
                "RASCUNHO", "ORC-001", LocalDate.now(), null, null, null, null,
                BigDecimal.valueOf(500), BigDecimal.ZERO, BigDecimal.valueOf(500),
                null, "PENDENTE", true, LocalDateTime.now(), List.of()
        );
    }

    // ===========================================================================
    // criar()
    // ===========================================================================

//    @Test
//    void criar_DeveRetornar201_ComBodyDoOrcamento() {
//        OrcamentoRequestDto request = buildRequest();
//        when(service.criarEGerarPdf(request)).thenReturn(orcamento);
//        when(mapper.toResponse(orcamento)).thenReturn(responseDto);
//
//        ResponseEntity<OrcamentoResponseDto> response = controller.criar(request);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(responseDto, response.getBody());
//        verify(service).criarEGerarPdf(request);
//        verify(mapper).toResponse(orcamento);
//    }

    // ===========================================================================
    // buscarPorId()
    // ===========================================================================

    @Test
    void buscarPorId_DeveRetornar200_ComOrcamento() {
        when(service.buscarPorId(1)).thenReturn(orcamento);
        when(mapper.toResponse(orcamento)).thenReturn(responseDto);

        ResponseEntity<OrcamentoResponseDto> response = controller.buscarPorId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void buscarPorId_DevePropagar_OrcamentoNaoEncontradoException() {
        when(service.buscarPorId(999)).thenThrow(new OrcamentoNaoEncontradoException());

        assertThrows(OrcamentoNaoEncontradoException.class, () -> controller.buscarPorId(999));
    }

    // ===========================================================================
    // listar()
    // ===========================================================================

    @Test
    void listar_DeveRetornar200_ComListaDeOrcamentos() {
        Page<Orcamento> page = new PageImpl<>(List.of(orcamento));
        when(service.listar(any(Pageable.class))).thenReturn(page);
        when(mapper.toResponse(orcamento)).thenReturn(responseDto);

        ResponseEntity<Page<OrcamentoResponseDto>> response = controller.listar(Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(responseDto, response.getBody().getContent().get(0));
    }

    @Test
    void listar_DeveRetornar200_ComListaVazia() {
        when(service.listar(any(Pageable.class))).thenReturn(Page.empty());

        ResponseEntity<Page<OrcamentoResponseDto>> response = controller.listar(Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // ===========================================================================
    // listarPorPedido()
    // ===========================================================================

    @Test
    void listarPorPedido_DeveRetornar200_ComOrcamentosVinculados() {
        Page<Orcamento> page = new PageImpl<>(List.of(orcamento));
        when(service.listarPorPedido(eq(10), any(Pageable.class))).thenReturn(page);
        when(mapper.toResponse(orcamento)).thenReturn(responseDto);

        ResponseEntity<Page<OrcamentoResponseDto>> response = controller.listarPorPedido(10, Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void listarPorPedido_DeveRetornar200_ComListaVazia() {
        when(service.listarPorPedido(eq(99), any(Pageable.class))).thenReturn(Page.empty());

        ResponseEntity<Page<OrcamentoResponseDto>> response = controller.listarPorPedido(99, Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // ===========================================================================
    // atualizar()
    // ===========================================================================

    @Test
    void atualizar_DeveRetornar200_ComOrcamentoAtualizado() {
        OrcamentoRequestDto request = buildRequest();
        when(service.atualizar(1, request)).thenReturn(orcamento);
        when(mapper.toResponse(orcamento)).thenReturn(responseDto);

        ResponseEntity<OrcamentoResponseDto> response = controller.atualizar(1, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void atualizar_DevePropagar_OrcamentoNaoEncontradoException() {
        OrcamentoRequestDto request = buildRequest();
        when(service.atualizar(eq(999), any())).thenThrow(new OrcamentoNaoEncontradoException());

        assertThrows(OrcamentoNaoEncontradoException.class, () -> controller.atualizar(999, request));
    }

    // ===========================================================================
    // deletar()
    // ===========================================================================

    @Test
    void deletar_DeveRetornar204_SemBody() {
        doNothing().when(service).deletar(1);

        ResponseEntity<Void> response = controller.deletar(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(service).deletar(1);
    }

    @Test
    void deletar_DevePropagar_OrcamentoNaoEncontradoException() {
        doThrow(new OrcamentoNaoEncontradoException()).when(service).deletar(999);

        assertThrows(OrcamentoNaoEncontradoException.class, () -> controller.deletar(999));
    }

    // ===========================================================================
    // atualizarStatus()
    // ===========================================================================

    @Test
    void atualizarStatus_DeveRetornar200_ComStatusAtualizado() {
        AtualizarStatusRequestDto body = new AtualizarStatusRequestDto("APROVADO", null);
        when(service.atualizarStatus(1, "APROVADO", null)).thenReturn(orcamento);
        when(mapper.toResponse(orcamento)).thenReturn(responseDto);

        ResponseEntity<OrcamentoResponseDto> response = controller.atualizarStatus(1, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void atualizarStatus_UsarStatusPadraoEnviado_QuandoStatusNulo() {
        AtualizarStatusRequestDto body = new AtualizarStatusRequestDto(null, null);
        when(service.atualizarStatus(1, "ENVIADO", null)).thenReturn(orcamento);
        when(mapper.toResponse(orcamento)).thenReturn(responseDto);

        controller.atualizarStatus(1, body);

        verify(service).atualizarStatus(1, "ENVIADO", null);
    }

    @Test
    void atualizarStatus_PassarPdfPath_QuandoFornecido() {
        AtualizarStatusRequestDto body = new AtualizarStatusRequestDto("APROVADO", "/pdfs/orcamento_001.pdf");
        when(service.atualizarStatus(1, "APROVADO", "/pdfs/orcamento_001.pdf")).thenReturn(orcamento);
        when(mapper.toResponse(orcamento)).thenReturn(responseDto);

        controller.atualizarStatus(1, body);

        verify(service).atualizarStatus(1, "APROVADO", "/pdfs/orcamento_001.pdf");
    }

    @Test
    void atualizarStatus_DevePropagar_OrcamentoNaoEncontradoException() {
        AtualizarStatusRequestDto body = new AtualizarStatusRequestDto("APROVADO", null);
        when(service.atualizarStatus(eq(999), anyString(), any()))
                .thenThrow(new OrcamentoNaoEncontradoException());

        assertThrows(OrcamentoNaoEncontradoException.class, () -> controller.atualizarStatus(999, body));
    }

    // ===========================================================================
    // baixarPdf()
    // ===========================================================================

    @Test
    void baixarPdf_DeveRetornar200_ComConteudoPdf() {
        byte[] pdfBytes = "conteudo-pdf".getBytes();
        when(service.buscarPorId(1)).thenReturn(orcamento);
        when(pdfCacheService.obterPorNumeroOrcamento("ORC-001")).thenReturn(pdfBytes);

        ResponseEntity<?> response = controller.baixarPdf(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertArrayEquals(pdfBytes, (byte[]) response.getBody());
    }

    @Test
    void baixarPdf_DeveRetornar404_QuandoPdfNaoEncontrado() {
        when(service.buscarPorId(1)).thenReturn(orcamento);
        when(pdfCacheService.obterPorNumeroOrcamento("ORC-001")).thenReturn(null);

        ResponseEntity<?> response = controller.baixarPdf(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void baixarPdf_DeveRetornar404_QuandoNumeroOrcamentoNulo() {
        orcamento.setNumeroOrcamento(null);
        when(service.buscarPorId(1)).thenReturn(orcamento);

        ResponseEntity<?> response = controller.baixarPdf(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(pdfCacheService, never()).obterPorNumeroOrcamento(any());
    }

    @Test
    void baixarPdf_DeveIncluirContentDisposition_NoHeader() {
        byte[] pdfBytes = "conteudo".getBytes();
        when(service.buscarPorId(1)).thenReturn(orcamento);
        when(pdfCacheService.obterPorNumeroOrcamento("ORC-001")).thenReturn(pdfBytes);

        ResponseEntity<?> response = controller.baixarPdf(1);

        String disposition = response.getHeaders().getFirst("Content-Disposition");
        assertNotNull(disposition);
        assertTrue(disposition.contains("ORC-001"));
    }

    // ===========================================================================
    // obterPdfPorNumero()
    // ===========================================================================

    @Test
    void obterPdfPorNumero_DeveRetornar200_ComConteudoPdf() {
        byte[] pdfBytes = "pdf-bytes".getBytes();
        when(pdfCacheService.obterPorNumeroOrcamento("ORC-001")).thenReturn(pdfBytes);

        ResponseEntity<?> response = controller.obterPdfPorNumero("ORC-001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertArrayEquals(pdfBytes, (byte[]) response.getBody());
    }

    @Test
    void obterPdfPorNumero_DeveRetornar404_QuandoPdfNaoEncontrado() {
        when(pdfCacheService.obterPorNumeroOrcamento("ORC-INEXISTENTE")).thenReturn(null);

        ResponseEntity<?> response = controller.obterPdfPorNumero("ORC-INEXISTENTE");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ===========================================================================
    // obterReportPorNumero()
    // ===========================================================================

    @Test
    void obterReportPorNumero_DeveRetornar200_ComReportDto() {
        byte[] pdfBytes = "pdf-bytes".getBytes();
        when(pdfCacheService.obterPorNumeroOrcamento("ORC-001")).thenReturn(pdfBytes);

        ResponseEntity<?> response = controller.obterReportPorNumero("ORC-001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(ReportDto.class, response.getBody());

        ReportDto report = (ReportDto) response.getBody();
        assertArrayEquals(pdfBytes, report.content());
        assertEquals(MediaType.APPLICATION_PDF_VALUE, report.mimeType());
        assertTrue(report.fileName().contains("ORC-001"));
    }

    @Test
    void obterReportPorNumero_DeveRetornar404_QuandoPdfNaoEncontrado() {
        when(pdfCacheService.obterPorNumeroOrcamento("ORC-INEXISTENTE")).thenReturn(null);

        ResponseEntity<?> response = controller.obterReportPorNumero("ORC-INEXISTENTE");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ===========================================================================
    // verificarStatusPdfCache()
    // ===========================================================================

    @Test
    @SuppressWarnings("unchecked")
    void verificarStatusPdfCache_DeveRetornar200_QuandoPdfPronto() {
        byte[] pdfBytes = "pdf-bytes".getBytes();
        when(pdfCacheService.obterPorNumeroOrcamento("ORC-001")).thenReturn(pdfBytes);

        ResponseEntity<?> response = controller.verificarStatusPdfCache("ORC-001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("pronto"));
        assertEquals("PDF pronto para download", body.get("mensagem"));
        assertEquals("ORC-001", body.get("numeroOrcamento"));
        assertEquals((long) pdfBytes.length, body.get("tamanho"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void verificarStatusPdfCache_DeveRetornar200_QuandoPdfAindaNaoGerado() {
        when(pdfCacheService.obterPorNumeroOrcamento("ORC-002")).thenReturn(null);

        ResponseEntity<?> response = controller.verificarStatusPdfCache("ORC-002");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("pronto"));
        assertEquals("PDF ainda não foi gerado", body.get("mensagem"));
        assertEquals(0L, body.get("tamanho"));
    }

    // ===========================================================================
    // streamProgresso()
    // ===========================================================================

    @Test
    void streamProgresso_DeveRetornarSseEmitter_CriandoViaService() {
        SseEmitter emitter = new SseEmitter();
        when(sseService.criarEmitter("1")).thenReturn(emitter);

        SseEmitter resultado = controller.streamProgresso("1");

        assertNotNull(resultado);
        assertEquals(emitter, resultado);
        verify(sseService).criarEmitter("1");
    }

    // ===========================================================================
    // Helpers
    // ===========================================================================

    private OrcamentoRequestDto buildRequest() {
        return new OrcamentoRequestDto(
                10, 1, null, "ORC-001", LocalDate.now(),
                null, null, null, null,
                BigDecimal.valueOf(500), BigDecimal.ZERO, BigDecimal.valueOf(500), null
        );
    }
}
