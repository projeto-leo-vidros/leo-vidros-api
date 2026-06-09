package com.project.extension.service.pedido;

import com.project.extension.entity.*;
import com.project.extension.exception.naoencontrado.PedidoNaoEncontradoException;
import com.project.extension.repository.HistoricoEstoqueRepository;
import com.project.extension.repository.OrcamentoRepository;
import com.project.extension.repository.PedidoRepository;
import com.project.extension.service.*;
import com.project.extension.strategy.pedido.PedidoContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService — testes unitários")
class PedidoServiceTests {

    @Mock private PedidoRepository repository;
    @Mock private EtapaService etapaService;
    @Mock private PedidoContext pedidoContext;
    @Mock private AgendamentoService agendamentoService;
    @Mock private HistoricoEstoqueRepository historicoEstoqueRepository;
    @Mock private OrcamentoRepository orcamentoRepository;
    @Mock private EstoqueService estoqueService;
    @Mock private LogService logService;

    @InjectMocks
    private PedidoService service;

    private Pedido pedidoEntrada;
    private Pedido pedidoSalvo;

    @BeforeEach
    void setUp() {
        pedidoEntrada = new Pedido();
        pedidoEntrada.setTipoPedido("PRODUTO");
        pedidoEntrada.setValorTotal(BigDecimal.valueOf(500));

        pedidoSalvo = new Pedido();
        pedidoSalvo.setId(1);
        pedidoSalvo.setTipoPedido("PRODUTO");
        pedidoSalvo.setValorTotal(BigDecimal.valueOf(500));
        pedidoSalvo.setAtivo(true);
    }

    // ── cadastrar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("cadastrar: salva pedido via context e retorna com ID")
    void cadastrar_sucesso() {
        when(pedidoContext.criar(any(Pedido.class))).thenReturn(pedidoEntrada);
        when(repository.save(any(Pedido.class))).thenReturn(pedidoSalvo);

        Pedido resultado = service.cadastrar(pedidoEntrada);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        verify(pedidoContext).criar(pedidoEntrada);
        verify(repository).save(pedidoEntrada);
        verify(logService).success(anyString());
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId: retorna pedido quando existe")
    void buscarPorId_sucesso() {
        when(repository.findById(1)).thenReturn(Optional.of(pedidoSalvo));

        Pedido resultado = service.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
    }

    @Test
    @DisplayName("buscarPorId: lança PedidoNaoEncontradoException quando não existe")
    void buscarPorId_naoEncontrado_lancaExcecao() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(PedidoNaoEncontradoException.class, () -> service.buscarPorId(999));
    }

    // ── listar ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("listar: retorna página de pedidos")
    void listar_retornaPagina() {
        Page<Pedido> page = new PageImpl<>(List.of(pedidoSalvo));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Pedido> resultado = service.listar(Pageable.unpaged());

        assertEquals(1, resultado.getTotalElements());
        assertSame(pedidoSalvo, resultado.getContent().get(0));
    }

    // ── editar ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("editar: atualiza via context e persiste")
    void editar_sucesso() {
        Pedido atualizado = new Pedido();
        atualizado.setValorTotal(BigDecimal.valueOf(800));

        when(repository.findById(1)).thenReturn(Optional.of(pedidoSalvo));
        when(pedidoContext.editar(any(Pedido.class), any(Pedido.class))).thenReturn(atualizado);
        when(repository.save(any(Pedido.class))).thenReturn(atualizado);

        Pedido resultado = service.editar(1, atualizado);

        assertEquals(BigDecimal.valueOf(800), resultado.getValorTotal());
        verify(pedidoContext).editar(pedidoSalvo, atualizado);
    }

    @Test
    @DisplayName("editar: lança PedidoNaoEncontradoException quando ID inválido")
    void editar_naoEncontrado_lancaExcecao() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(PedidoNaoEncontradoException.class,
                () -> service.editar(999, new Pedido()));
    }

    // ── deletar ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deletar: sem serviço nem itens — remove pedido diretamente")
    void deletar_semServico_sucesso() {
        pedidoSalvo.setServico(null);
        pedidoSalvo.setItensPedido(Collections.emptyList());

        when(repository.findById(1)).thenReturn(Optional.of(pedidoSalvo));
        when(pedidoContext.deletar(pedidoSalvo)).thenReturn(pedidoSalvo);

        service.deletar(1);

        verify(pedidoContext).deletar(pedidoSalvo);
        verify(orcamentoRepository).deleteByPedidoId(1);
        verify(historicoEstoqueRepository).deleteByPedidoId(1);
        verify(repository).delete(pedidoSalvo);
    }

    @Test
    @DisplayName("deletar: com serviço — cancela agendamentos antes de deletar")
    void deletar_comServico_cancelaAgendamentos() {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(10);
        agendamento.setFuncionarios(new java.util.ArrayList<>());
        agendamento.setAgendamentoProdutos(new java.util.ArrayList<>());

        Servico servico = new Servico();
        servico.setId(5);
        servico.setAgendamentos(new java.util.ArrayList<>(List.of(agendamento)));

        pedidoSalvo.setServico(servico);
        pedidoSalvo.setItensPedido(Collections.emptyList());

        when(repository.findById(1)).thenReturn(Optional.of(pedidoSalvo));
        when(pedidoContext.deletar(pedidoSalvo)).thenReturn(pedidoSalvo);

        service.deletar(1);

        verify(agendamentoService).deletar(10);
        verify(repository).delete(pedidoSalvo);
    }

    // ── listarPorTipo ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPedidosPorTipo: filtra pelo tipo informado")
    void listarPorTipo_retornaFiltrado() {
        Page<Pedido> page = new PageImpl<>(List.of(pedidoSalvo));
        when(repository.findByTipoPedidoIgnoreCase(eq("PRODUTO"), any(Pageable.class))).thenReturn(page);

        Page<Pedido> resultado = service.listarPedidosPorTipo("PRODUTO", Pageable.unpaged());

        assertEquals(1, resultado.getTotalElements());
    }

    // ── listarPorEtapa ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPedidosPorTipoENomeDaEtapa: busca etapa e filtra pedidos")
    void listarPorEtapa_sucesso() {
        Etapa etapa = new Etapa();
        etapa.setNome("AGUARDANDO ORÇAMENTO");

        Page<Pedido> page = new PageImpl<>(List.of(pedidoSalvo));
        when(etapaService.buscarPorTipoAndEtapa("PEDIDO", "AGUARDANDO ORÇAMENTO")).thenReturn(etapa);
        when(repository.findAllByServico_Etapa(eq(etapa), any(Pageable.class))).thenReturn(page);

        Page<Pedido> resultado = service.listarPedidosPorTipoENomeDaEtapa("AGUARDANDO ORÇAMENTO", Pageable.unpaged());

        assertEquals(1, resultado.getTotalElements());
        verify(etapaService).buscarPorTipoAndEtapa("PEDIDO", "AGUARDANDO ORÇAMENTO");
    }
}
