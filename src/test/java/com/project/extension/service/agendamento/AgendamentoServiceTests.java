package com.project.extension.service.agendamento;

import com.project.extension.entity.*;
import com.project.extension.exception.naoencontrado.AgendamentoNaoEncontradoException;
import com.project.extension.repository.AgendamentoRepository;
import com.project.extension.repository.PedidoRepository;
import com.project.extension.service.*;
import com.project.extension.strategy.agendamento.AgendamentoContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AgendamentoService — testes unitários")
class AgendamentoServiceTests {

    @Mock private AgendamentoRepository repository;
    @Mock private EnderecoService enderecoService;
    @Mock private FuncionarioService funcionarioService;
    @Mock private StatusService statusService;
    @Mock private AgendamentoContext agendamentoContext;
    @Mock private ServicoService servicoService;
    @Mock private EtapaService etapaService;
    @Mock private LogService logService;
    @Mock private EstoqueService estoqueService;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private PedidoConclusaoService pedidoConclusaoService;

    @InjectMocks
    private AgendamentoService service;

    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        agendamento = new Agendamento();
        agendamento.setId(1);
        agendamento.setAtivo(true);
        agendamento.setTipoAgendamento(TipoAgendamento.ORCAMENTO);
        agendamento.setDataAgendamento(LocalDate.now().plusDays(1));
        agendamento.setInicioAgendamento(LocalTime.of(9, 0));
        agendamento.setFimAgendamento(LocalTime.of(10, 0));
        agendamento.setFuncionarios(new ArrayList<>());
        agendamento.setAgendamentoProdutos(new ArrayList<>());

        Status status = new Status();
        status.setId(1);
        status.setNome("PENDENTE");
        agendamento.setStatusAgendamento(status);
    }

    // ── salvar ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("salvar: processa via context e persiste agendamento")
    void salvar_sucesso() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1);
        funcionario.setNome("Técnico");
        funcionario.setAtivo(true);

        Agendamento entrada = new Agendamento();
        entrada.setTipoAgendamento(TipoAgendamento.ORCAMENTO);
        entrada.setFuncionarios(new ArrayList<>(List.of(funcionario)));

        when(agendamentoContext.processarAgendamento(any())).thenReturn(agendamento);
        when(repository.save(any())).thenReturn(agendamento);

        Agendamento salvo = service.salvar(entrada);

        assertNotNull(salvo);
        assertEquals(1, salvo.getId());
        verify(agendamentoContext).processarAgendamento(entrada);
        verify(repository).save(agendamento);
        verify(logService).success(anyString());
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId: retorna agendamento quando encontrado")
    void buscarPorId_sucesso() {
        when(repository.findById(1)).thenReturn(Optional.of(agendamento));

        Agendamento encontrado = service.buscarPorId(1);

        assertNotNull(encontrado);
        assertEquals(1, encontrado.getId());
    }

    @Test
    @DisplayName("buscarPorId: lança AgendamentoNaoEncontradoException quando não existe")
    void buscarPorId_naoEncontrado_lancaExcecao() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(AgendamentoNaoEncontradoException.class,
                () -> service.buscarPorId(999));
    }

    // ── buscarTodos ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarTodos: retorna página de agendamentos")
    void buscarTodos_retornaPagina() {
        Page<Agendamento> page = new PageImpl<>(List.of(agendamento));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Agendamento> resultado = service.buscarTodos(Pageable.unpaged());

        assertEquals(1, resultado.getTotalElements());
        assertSame(agendamento, resultado.getContent().get(0));
    }

    // ── editar ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("editar: atualiza dados básicos e persiste")
    void editar_sucesso() {
        Agendamento origem = new Agendamento();
        origem.setTipoAgendamento(TipoAgendamento.SERVICO);
        origem.setDataAgendamento(LocalDate.now().plusDays(2));
        origem.setInicioAgendamento(LocalTime.of(14, 0));
        origem.setFimAgendamento(LocalTime.of(16, 0));
        // sem funcionários: pula o bloco de validação de funcionários no editar
        origem.setFuncionarios(null);
        origem.setAgendamentoProdutos(new ArrayList<>());

        Status statusMock = new Status();
        statusMock.setTipo("AGENDAMENTO");
        statusMock.setNome("PENDENTE");
        origem.setStatusAgendamento(statusMock);

        when(repository.findById(1)).thenReturn(Optional.of(agendamento));
        when(statusService.buscarOuCriarPorTipoENome(anyString(), anyString())).thenReturn(statusMock);
        when(repository.save(any())).thenReturn(agendamento);

        Agendamento atualizado = service.editar(origem, 1);

        assertNotNull(atualizado);
        verify(repository).save(any());
    }

    @Test
    @DisplayName("editar: lança AgendamentoNaoEncontradoException quando ID inválido")
    void editar_naoEncontrado_lancaExcecao() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(AgendamentoNaoEncontradoException.class,
                () -> service.editar(new Agendamento(), 999));
    }

    // ── deletar ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deletar: limpa funcionários, produtos e executa soft delete")
    void deletar_semEstoque_sucesso() {
        agendamento.setServico(null);

        when(repository.findById(1)).thenReturn(Optional.of(agendamento));

        service.deletar(1);

        assertTrue(agendamento.getFuncionarios().isEmpty());
        verify(repository).delete(agendamento);
        verify(repository).flush();
    }

    @Test
    @DisplayName("deletar: com produtos reservados — libera estoque antes de deletar")
    void deletar_comProdutosReservados_liberaEstoque() {
        Produto produto = new Produto();
        produto.setId(10);
        produto.setNome("Vidro 10mm");

        AgendamentoProduto ap = new AgendamentoProduto();
        ap.setProduto(produto);
        ap.setQuantidadeReservada(BigDecimal.valueOf(3));

        agendamento.setAgendamentoProdutos(new ArrayList<>(List.of(ap)));
        agendamento.setServico(null);

        Status statusPendente = new Status();
        statusPendente.setNome("PENDENTE");
        agendamento.setStatusAgendamento(statusPendente);

        when(repository.findById(1)).thenReturn(Optional.of(agendamento));

        service.deletar(1);

        verify(estoqueService).liberarProduto(eq(produto), eq(BigDecimal.valueOf(3)));
        verify(repository).delete(agendamento);
    }

    // ── adicionarFuncionario ────────────────────────────────────────────────────

    @Test
    @DisplayName("adicionarFuncionario: vincula funcionário ativo ao agendamento")
    void adicionarFuncionario_sucesso() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(5);
        funcionario.setNome("João");
        funcionario.setAtivo(true);

        when(repository.findById(1)).thenReturn(Optional.of(agendamento));
        when(funcionarioService.buscarPorId(5)).thenReturn(funcionario);
        when(funcionarioService.temConflito(eq(5), any(), any(), any())).thenReturn(false);
        when(repository.save(any())).thenReturn(agendamento);

        Agendamento resultado = service.adicionarFuncionario(1, 5);

        assertNotNull(resultado);
        assertTrue(agendamento.getFuncionarios().contains(funcionario));
    }

    @Test
    @DisplayName("adicionarFuncionario: funcionário inativo lança RegraNegocioException")
    void adicionarFuncionario_inativo_lancaExcecao() {
        Funcionario funcionarioInativo = new Funcionario();
        funcionarioInativo.setId(6);
        funcionarioInativo.setAtivo(false);

        when(repository.findById(1)).thenReturn(Optional.of(agendamento));
        when(funcionarioService.buscarPorId(6)).thenReturn(funcionarioInativo);

        assertThrows(com.project.extension.exception.RegraNegocioException.class,
                () -> service.adicionarFuncionario(1, 6));
    }
}
