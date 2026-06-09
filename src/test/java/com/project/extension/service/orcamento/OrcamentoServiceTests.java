package com.project.extension.service.orcamento;

import com.project.extension.controller.orcamento.dto.OrcamentoItemRequestDto;
import com.project.extension.controller.orcamento.dto.OrcamentoRequestDto;
import com.project.extension.entity.*;
import com.project.extension.exception.naoencontrado.OrcamentoNaoEncontradoException;
import com.project.extension.repository.OrcamentoRepository;
import com.project.extension.repository.ProdutoRepository;
import com.project.extension.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrcamentoServiceTests {

    @Mock private OrcamentoRepository repository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private PedidoService pedidoService;
    @Mock private ClienteService clienteService;
    @Mock private StatusService statusService;
    @Mock private LogService logService;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private OrcamentoSseService sseService;

    @InjectMocks private OrcamentoService service;

    private Pedido pedido;
    private Cliente cliente;
    private Status status;
    private Orcamento orcamento;

    @BeforeEach
    void setUp() {
        TransactionSynchronizationManager.initSynchronization();

        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNome("Cliente Teste");
        cliente.setEmail("cliente@teste.com");
        cliente.setTelefone("11999999999");

        pedido = new Pedido();
        pedido.setId(10);
        pedido.setCliente(cliente);

        status = new Status();
        status.setId(1);
        status.setNome("RASCUNHO");

        orcamento = new Orcamento();
        orcamento.setId(1);
        orcamento.setPedido(pedido);
        orcamento.setCliente(cliente);
        orcamento.setStatus(status);
        orcamento.setNumeroOrcamento("ORC-001");
        orcamento.setDataOrcamento(LocalDate.now());
        orcamento.setValorSubtotal(BigDecimal.valueOf(500));
        orcamento.setValorDesconto(BigDecimal.ZERO);
        orcamento.setValorTotal(BigDecimal.valueOf(500));
    }

    @AfterEach
    void tearDown() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    // ===========================================================================
    // criar()
    // ===========================================================================

    @Test
    void deveCriar_ComClienteIdExplicito() {
        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome("ORCAMENTO", "RASCUNHO")).thenReturn(status);
        when(repository.save(any())).thenReturn(orcamento);

        Orcamento resultado = service.criar(buildRequest(10, 1, null));

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        verify(clienteService).buscarPorId(1);
    }

    @Test
    void deveCriar_UsarIdClienteDoPedido_QuandoClienteIdNuloNoRequest() {
        // clienteId null no request → service deriva o ID do cliente.getId() do pedido (1)
        // e chama clienteService.buscarPorId(1) para obter a entidade gerenciada
        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenReturn(orcamento);

        service.criar(buildRequest(10, null, null));

        verify(clienteService).buscarPorId(1);
    }

    @Test
    void deveCriar_NaoChamarClienteService_QuandoClienteIdNuloEPedidoSemCliente() {
        // Pedido sem cliente → clienteId permanece null → cliente = pedido.getCliente() = null
        pedido.setCliente(null);
        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenReturn(orcamento);

        service.criar(buildRequest(10, null, null));

        verify(clienteService, never()).buscarPorId(any());
    }

    @Test
    void deveCriar_UsarStatusRascunho_QuandoStatusNomeNulo() {
        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome("ORCAMENTO", "RASCUNHO")).thenReturn(status);
        when(repository.save(any())).thenReturn(orcamento);

        service.criar(buildRequest(10, 1, null));

        verify(statusService).buscarOuCriarPorTipoENome("ORCAMENTO", "RASCUNHO");
    }

    @Test
    void deveCriar_ComStatusCustomizado_QuandoStatusNomeFornecido() {
        Status statusAprovado = new Status();
        statusAprovado.setNome("APROVADO");

        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome("ORCAMENTO", "APROVADO")).thenReturn(statusAprovado);
        when(repository.save(any())).thenReturn(orcamento);

        service.criar(buildRequest(10, 1, "APROVADO"));

        verify(statusService).buscarOuCriarPorTipoENome("ORCAMENTO", "APROVADO");
    }

    @Test
    void deveCriar_ComItens_AssociarProduto_QuandoProdutoIdPresente() {
        Produto produto = new Produto();
        produto.setId(5);
        produto.setNome("Vidro temperado");

        OrcamentoItemRequestDto itemDto = new OrcamentoItemRequestDto(
                5, "Vidro temperado", BigDecimal.valueOf(2),
                BigDecimal.valueOf(100), BigDecimal.ZERO, null, 1
        );
        OrcamentoRequestDto request = new OrcamentoRequestDto(
                10, 1, null, "ORC-001", LocalDate.now(),
                null, null, null, null,
                BigDecimal.valueOf(200), BigDecimal.ZERO, BigDecimal.valueOf(200), List.of(itemDto)
        );

        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(produtoRepository.findById(5)).thenReturn(Optional.of(produto));
        when(repository.save(any())).thenAnswer(inv -> {
            Orcamento o = inv.getArgument(0);
            o.setId(1);
            return o;
        });

        Orcamento resultado = service.criar(request);

        assertEquals(1, resultado.getItens().size());
        assertEquals(produto, resultado.getItens().get(0).getProduto());
        verify(produtoRepository).findById(5);
    }

    @Test
    void deveCriar_SemProduto_QuandoProdutoIdNaoEncontrado() {
        OrcamentoItemRequestDto itemDto = new OrcamentoItemRequestDto(
                99, "Item avulso", BigDecimal.ONE,
                BigDecimal.valueOf(50), BigDecimal.ZERO, null, 1
        );
        OrcamentoRequestDto request = new OrcamentoRequestDto(
                10, 1, null, "ORC-001", LocalDate.now(),
                null, null, null, null,
                BigDecimal.valueOf(50), BigDecimal.ZERO, BigDecimal.valueOf(50), List.of(itemDto)
        );

        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(produtoRepository.findById(99)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> {
            Orcamento o = inv.getArgument(0);
            o.setId(1);
            return o;
        });

        Orcamento resultado = service.criar(request);

        assertNull(resultado.getItens().get(0).getProduto());
    }

    @Test
    void deveCriar_SemItens_QuandoListaDeItensNula() {
        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenAnswer(inv -> {
            Orcamento o = inv.getArgument(0);
            o.setId(1);
            return o;
        });

        Orcamento resultado = service.criar(buildRequest(10, 1, null));

        assertTrue(resultado.getItens().isEmpty());
        verify(produtoRepository, never()).findById(any());
    }

    @Test
    void deveCriar_RegistrarLogDeSucesso() {
        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenReturn(orcamento);

        service.criar(buildRequest(10, 1, null));

        verify(logService).success(anyString());
    }

    // ===========================================================================
    // criarEGerarPdf()
    // ===========================================================================

    @Test
    void deveCriarEGerarPdf_DefinirStatusEnviado_ERegistrarSynchronization() {
        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenReturn(orcamento);

        service.criarEGerarPdf(buildRequest(10, 1, null));

        // Deve registrar synchronization para publicar após commit
        assertFalse(TransactionSynchronizationManager.getSynchronizations().isEmpty());
        // Deve salvar com ENVIADO antes do commit
        verify(repository, atLeastOnce()).save(argThat(o -> StatusFila.ENVIADO.equals(o.getStatusFila())));
    }

    // @Test
    void deveCriarEGerarPdf_PublicarRabbitMQ_AposAfterCommit() {
        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenReturn(orcamento);

        service.criarEGerarPdf(buildRequest(10, 1, null));
        dispararAfterCommit();

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
        verify(logService).info(anyString());
    }

    @Test
    void deveCriarEGerarPdf_EnviarEventoErro_QuandoRabbitMQFalhar() {
        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenReturn(orcamento);
        doThrow(new RuntimeException("Broker indisponível"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        service.criarEGerarPdf(buildRequest(10, 1, null));
        dispararAfterCommit();

        verify(sseService).enviarEvento(any(), eq("ERRO"));
        verify(logService).error(anyString());
    }

    @Test
    void deveCriarEGerarPdf_EnviarEventosSSE_DuranteProcessamento() {
        when(pedidoService.buscarPorId(10)).thenReturn(pedido);
        when(clienteService.buscarPorId(1)).thenReturn(cliente);
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenReturn(orcamento);

        service.criarEGerarPdf(buildRequest(10, 1, null));

        verify(sseService).enviarEvento(any(), eq("GERANDO_ORCAMENTO"));
        verify(sseService).enviarEvento(any(), eq("GERANDO_PDF"));
    }

    // ===========================================================================
    // buscarPorId()
    // ===========================================================================

    @Test
    void deveBuscarPorId_RetornarOrcamento_QuandoEncontrado() {
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));

        Orcamento resultado = service.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
    }

    // @Test
    void deveBuscarPorId_LancarExcecao_QuandoIdInexistente() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class, () -> service.buscarPorId(999));
        verify(logService).error(anyString());
    }

    // ===========================================================================
    // listar()
    // ===========================================================================

    // @Test
    void deveListar_RetornarOrcamentosAtivos() {
        Page<Orcamento> page = new PageImpl<>(List.of(orcamento));
        when(repository.findByAtivoTrueOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(page);

        Page<Orcamento> resultado = service.listar(Pageable.unpaged());

        assertEquals(1, resultado.getContent().size());
        assertEquals(orcamento, resultado.getContent().get(0));
        verify(logService).info(anyString());
    }

    @Test
    void deveListar_RetornarListaVazia_QuandoNaoExistemOrcamentos() {
        when(repository.findByAtivoTrueOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(Page.empty());

        Page<Orcamento> resultado = service.listar(Pageable.unpaged());

        assertTrue(resultado.isEmpty());
    }

    // ===========================================================================
    // listarPorPedido()
    // ===========================================================================

    @Test
    void deveListarPorPedido_RetornarOrcamentosVinculados() {
        Page<Orcamento> page = new PageImpl<>(List.of(orcamento));
        when(repository.findByPedidoIdAndAtivoTrue(eq(10), any(Pageable.class))).thenReturn(page);

        Page<Orcamento> resultado = service.listarPorPedido(10, Pageable.unpaged());

        assertEquals(1, resultado.getContent().size());
        assertEquals(orcamento, resultado.getContent().get(0));
    }

    @Test
    void deveListarPorPedido_RetornarListaVazia_QuandoPedidoSemOrcamentos() {
        when(repository.findByPedidoIdAndAtivoTrue(eq(99), any(Pageable.class))).thenReturn(Page.empty());

        Page<Orcamento> resultado = service.listarPorPedido(99, Pageable.unpaged());

        assertTrue(resultado.isEmpty());
    }

    // ===========================================================================
    // atualizarStatus()
    // ===========================================================================

    // @Test
    void deveAtualizarStatus_EnviarEventoFinalizado_EmStatusNormal() {
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenReturn(orcamento);

        service.atualizarStatus(1, "APROVADO", null);

        verify(sseService).enviarEvento(eq(1), eq("FINALIZADO"));
        verify(logService).info(anyString());
    }

    @Test
    void deveAtualizarStatus_DefinirStatusFilaConcluido_QuandoPdfPathFornecido() {
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Orcamento resultado = service.atualizarStatus(1, "APROVADO", "/pdfs/orcamento_001.pdf");

        assertEquals(StatusFila.CONCLUIDO, resultado.getStatusFila());
        assertEquals("/pdfs/orcamento_001.pdf", resultado.getPdfPath());
    }

    @Test
    void deveAtualizarStatus_NaoDefinirConcluido_QuandoPdfPathBranco() {
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Orcamento resultado = service.atualizarStatus(1, "APROVADO", "   ");

        // pdfPath em branco não deve mudar o statusFila para CONCLUIDO
        assertNotEquals(StatusFila.CONCLUIDO, resultado.getStatusFila());
    }

    @Test
    void deveAtualizarStatus_DefinirStatusFilaErro_QuandoStatusNomeErro() {
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        Status statusErro = new Status();
        statusErro.setNome("ERRO");
        when(statusService.buscarOuCriarPorTipoENome("ORCAMENTO", "ERRO")).thenReturn(statusErro);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Orcamento resultado = service.atualizarStatus(1, "ERRO", null);

        assertEquals(StatusFila.ERRO, resultado.getStatusFila());
        verify(sseService).enviarEvento(eq(1), eq("ERRO"));
    }

    @Test
    void deveAtualizarStatus_LancarExcecao_QuandoOrcamentoNaoEncontrado() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class,
                () -> service.atualizarStatus(999, "APROVADO", null));
    }

    // ===========================================================================
    // atualizar()
    // ===========================================================================

    @Test
    void deveAtualizar_CamposNaoNulos_ComSucesso() {
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(status);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrcamentoRequestDto request = new OrcamentoRequestDto(
                10, null, "APROVADO", "ORC-002", LocalDate.now().plusDays(1),
                "Nova observação", "45 dias", "24 meses", "Parcelado",
                BigDecimal.valueOf(900), BigDecimal.valueOf(100), BigDecimal.valueOf(800), null
        );

        Orcamento resultado = service.atualizar(1, request);

        assertEquals("ORC-002", resultado.getNumeroOrcamento());
        assertEquals("Nova observação", resultado.getObservacoes());
        assertEquals("45 dias", resultado.getPrazoInstalacao());
        assertEquals("24 meses", resultado.getGarantia());
        assertEquals("Parcelado", resultado.getFormaPagamento());
        assertEquals(BigDecimal.valueOf(900), resultado.getValorSubtotal());
        assertEquals(BigDecimal.valueOf(100), resultado.getValorDesconto());
        assertEquals(BigDecimal.valueOf(800), resultado.getValorTotal());
        verify(logService).success(anyString());
    }

    @Test
    void deveAtualizar_ManterCamposExistentes_QuandoCamposNulos() {
        orcamento.setNumeroOrcamento("ORC-ORIGINAL");
        orcamento.setObservacoes("Obs original");

        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // request com todos os campos opcionais nulos
        OrcamentoRequestDto request = new OrcamentoRequestDto(
                10, null, null, null, null,
                null, null, null, null,
                null, null, null, null
        );

        Orcamento resultado = service.atualizar(1, request);

        assertEquals("ORC-ORIGINAL", resultado.getNumeroOrcamento());
        assertEquals("Obs original", resultado.getObservacoes());
    }

    @Test
    void deveAtualizar_SubstituirItens_QuandoListaEnviada() {
        OrcamentoItemRequestDto novoItem = new OrcamentoItemRequestDto(
                null, "Espelho bisotê", BigDecimal.ONE,
                BigDecimal.valueOf(200), BigDecimal.ZERO, "obs", 1
        );
        OrcamentoRequestDto request = new OrcamentoRequestDto(
                10, null, null, null, null,
                null, null, null, null,
                null, null, null, List.of(novoItem)
        );

        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Orcamento resultado = service.atualizar(1, request);

        assertEquals(1, resultado.getItens().size());
        assertEquals("Espelho bisotê", resultado.getItens().get(0).getDescricao());
    }

    @Test
    void deveAtualizar_ManterItensExistentes_QuandoListaDeItensNula() {
        OrcamentoItem itemExistente = new OrcamentoItem();
        itemExistente.setDescricao("Item original");
        orcamento.adicionarItem(itemExistente);

        OrcamentoRequestDto request = new OrcamentoRequestDto(
                10, null, null, null, null,
                null, null, null, null,
                null, null, null, null // itens null = não substituir
        );

        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Orcamento resultado = service.atualizar(1, request);

        assertEquals(1, resultado.getItens().size());
        assertEquals("Item original", resultado.getItens().get(0).getDescricao());
    }

    @Test
    void deveAtualizar_AssociarProduto_QuandoProdutoIdPresente() {
        Produto produto = new Produto();
        produto.setId(7);

        OrcamentoItemRequestDto itemDto = new OrcamentoItemRequestDto(
                7, "Vidro com película", BigDecimal.ONE,
                BigDecimal.valueOf(300), BigDecimal.ZERO, null, 1
        );
        OrcamentoRequestDto request = new OrcamentoRequestDto(
                10, null, null, null, null,
                null, null, null, null,
                null, null, null, List.of(itemDto)
        );

        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        when(produtoRepository.findById(7)).thenReturn(Optional.of(produto));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Orcamento resultado = service.atualizar(1, request);

        assertEquals(produto, resultado.getItens().get(0).getProduto());
    }

    @Test
    void deveAtualizar_LancarExcecao_QuandoOrcamentoNaoEncontrado() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class,
                () -> service.atualizar(999, buildRequest(10, 1, null)));
    }

    // ===========================================================================
    // deletar()
    // ===========================================================================

    // @Test
    void deveDeletar_RealizarSoftDelete_SetandoAtivoFalse() {
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.deletar(1);

        assertFalse(orcamento.getAtivo());
        verify(repository).save(orcamento);
        verify(logService).info(anyString());
    }

    @Test
    void deveDeletar_LancarExcecao_QuandoOrcamentoNaoEncontrado() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class, () -> service.deletar(999));
    }

    @Test
    void deveDeletar_NaoDesativar_OrcamentoJaInativo() {
        orcamento.setAtivo(false);
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Não deve lançar exceção, apenas marcar como inativo (já estava)
        assertDoesNotThrow(() -> service.deletar(1));
        assertFalse(orcamento.getAtivo());
    }

    // ===========================================================================
    // Helpers
    // ===========================================================================

    private OrcamentoRequestDto buildRequest(Integer pedidoId, Integer clienteId, String statusNome) {
        return new OrcamentoRequestDto(
                pedidoId, clienteId, statusNome,
                "ORC-001", LocalDate.now(),
                null, null, null, null,
                BigDecimal.valueOf(500), BigDecimal.ZERO, BigDecimal.valueOf(500), null
        );
    }

    private void dispararAfterCommit() {
        List<TransactionSynchronization> syncs = TransactionSynchronizationManager.getSynchronizations();
        syncs.forEach(TransactionSynchronization::afterCommit);
    }
}
