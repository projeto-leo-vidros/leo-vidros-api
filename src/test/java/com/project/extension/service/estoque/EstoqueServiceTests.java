package com.project.extension.service.estoque;

import com.project.extension.entity.*;
import com.project.extension.exception.naoencontrado.EstoqueNaoEncontradoException;
import com.project.extension.exception.naopodesernegativo.EstoqueNaoPodeSerNegativoException;
import com.project.extension.repository.AgendamentoProdutoRepository;
import com.project.extension.repository.EstoqueRepository;
import com.project.extension.repository.ItemPedidoRepository;
import com.project.extension.service.*;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("EstoqueService — testes unitários")
class EstoqueServiceTests {

    @Mock private EstoqueRepository repository;
    @Mock private AgendamentoProdutoRepository agendamentoProdutoRepository;
    @Mock private ItemPedidoRepository itemPedidoRepository;
    @Mock private ProdutoService produtoService;
    @Mock private HistoricoEstoqueService historicoService;
    @Mock private UsuarioService usuarioService;

    @InjectMocks
    private EstoqueService service;

    private Produto produto;
    private Estoque estoque;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1);
        produto.setNome("Vidro 8mm");
        produto.setAtivo(true);

        estoque = new Estoque();
        estoque.setId(1);
        estoque.setProduto(produto);
        estoque.setLocalizacao("Depósito A");
        estoque.setQuantidadeTotal(BigDecimal.valueOf(10));
        estoque.setQuantidadeDisponivel(BigDecimal.valueOf(10));
        estoque.setReservado(BigDecimal.ZERO);

        usuario = new Usuario();
        usuario.setId(99);
        usuario.setNome("Usuário Teste");

        // Configura SecurityContext para getUsuarioLogado()
        var auth = new UsernamePasswordAuthenticationToken("teste@leo.com", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Stubs compartilhados
        when(usuarioService.buscarPorEmail("teste@leo.com")).thenReturn(usuario);
        when(produtoService.buscarPorId(1)).thenReturn(produto);
        when(agendamentoProdutoRepository.somarReservasAtivasPorProdutoId(1)).thenReturn(BigDecimal.ZERO);
        when(itemPedidoRepository.somarReservasDetalheServicoAtivasPorProdutoId(eq(1), any())).thenReturn(BigDecimal.ZERO);
        when(repository.findByProdutoAndLocalizacaoForUpdate(produto, "Depósito A"))
                .thenReturn(Optional.of(estoque));
        when(repository.save(any(Estoque.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ── entrada ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("entrada: incrementa quantidade total e disponível")
    void entrada_deveIncrementarEstoque() {
        Estoque request = buildRequest(BigDecimal.valueOf(5));

        Estoque resultado = service.entrada(request);

        assertEquals(BigDecimal.valueOf(15), resultado.getQuantidadeTotal());
        assertEquals(BigDecimal.valueOf(15), resultado.getQuantidadeDisponivel());
        verify(historicoService).cadastrar(any(HistoricoEstoque.class));
    }

    @Test
    @DisplayName("entrada: quantidade zero lança IllegalArgumentException")
    void entrada_quantidadeZero_lancaExcecao() {
        Estoque request = buildRequest(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> service.entrada(request));
    }

    @Test
    @DisplayName("entrada: produto nulo lança IllegalArgumentException")
    void entrada_produtoNulo_lancaExcecao() {
        Estoque request = new Estoque();
        request.setQuantidadeTotal(BigDecimal.TEN);
        assertThrows(IllegalArgumentException.class, () -> service.entrada(request));
    }

    // ── saida ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saida: decrementa quantidade corretamente")
    void saida_deveDecrementarEstoque() {
        Estoque request = buildRequest(BigDecimal.valueOf(3));

        Estoque resultado = service.saida(request);

        assertEquals(BigDecimal.valueOf(7), resultado.getQuantidadeTotal());
        assertEquals(BigDecimal.valueOf(7), resultado.getQuantidadeDisponivel());
        verify(historicoService).cadastrar(any(HistoricoEstoque.class));
    }

    @Test
    @DisplayName("saida: estoque insuficiente lança EstoqueNaoPodeSerNegativoException")
    void saida_estoqueInsuficiente_lancaExcecao() {
        Estoque request = buildRequest(BigDecimal.valueOf(20));
        assertThrows(EstoqueNaoPodeSerNegativoException.class, () -> service.saida(request));
    }

    @Test
    @DisplayName("saida: quantidade exata disponível é permitida (boundary)")
    void saida_quantidadeExata_sucesso() {
        Estoque request = buildRequest(BigDecimal.valueOf(10));

        Estoque resultado = service.saida(request);

        assertEquals(BigDecimal.ZERO, resultado.getQuantidadeTotal());
    }

    // ── reservarProduto ───────────────────────────────────────────────────────

    @Test
    @DisplayName("reservarProduto: incrementa reservado e reduz disponível")
    void reservar_deveIncrementarReserva() {
        when(repository.findByProdutoIdForUpdate(1)).thenReturn(Optional.of(estoque));

        service.reservarProduto(produto, BigDecimal.valueOf(4));

        assertEquals(BigDecimal.valueOf(4), estoque.getReservado());
        assertEquals(BigDecimal.valueOf(6), estoque.getQuantidadeDisponivel());
    }

    @Test
    @DisplayName("reservarProduto: sem estoque disponível lança exceção")
    void reservar_semDisponivel_lancaExcecao() {
        // totalAtual = disponivel para que sincronizarReserva não altere o disponivel
        estoque.setQuantidadeTotal(BigDecimal.valueOf(2));
        estoque.setQuantidadeDisponivel(BigDecimal.valueOf(2));
        when(repository.findByProdutoIdForUpdate(1)).thenReturn(Optional.of(estoque));

        assertThrows(EstoqueNaoPodeSerNegativoException.class,
                () -> service.reservarProduto(produto, BigDecimal.valueOf(5)));
    }

    @Test
    @DisplayName("reservarProduto: estoque não encontrado lança EstoqueNaoEncontradoException")
    void reservar_estoqueNaoEncontrado_lancaExcecao() {
        when(repository.findByProdutoIdForUpdate(1)).thenReturn(Optional.empty());
        assertThrows(EstoqueNaoEncontradoException.class,
                () -> service.reservarProduto(produto, BigDecimal.ONE));
    }

    // ── liberarProduto ────────────────────────────────────────────────────────

    @Test
    @DisplayName("liberarProduto: decrementa reservado e devolve ao disponível")
    void liberar_deveDecrementarReserva() {
        estoque.setReservado(BigDecimal.valueOf(5));
        estoque.setQuantidadeDisponivel(BigDecimal.valueOf(5));
        // Faz a sincronização concordar com o reservado atual para não o sobrescrever
        when(agendamentoProdutoRepository.somarReservasAtivasPorProdutoId(1)).thenReturn(BigDecimal.valueOf(5));
        when(repository.findByProdutoIdForUpdate(1)).thenReturn(Optional.of(estoque));

        service.liberarProduto(produto, BigDecimal.valueOf(3));

        assertEquals(BigDecimal.valueOf(2), estoque.getReservado());
        assertEquals(BigDecimal.valueOf(8), estoque.getQuantidadeDisponivel());
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId: retorna estoque quando encontrado")
    void buscarPorId_sucesso() {
        when(repository.findById(1)).thenReturn(Optional.of(estoque));

        Estoque encontrado = service.buscarPorId(1);

        assertNotNull(encontrado);
        assertEquals(1, encontrado.getId());
    }

    @Test
    @DisplayName("buscarPorId: lança EstoqueNaoEncontradoException quando não existe")
    void buscarPorId_naoEncontrado_lancaExcecao() {
        when(repository.findById(999)).thenReturn(Optional.empty());
        assertThrows(EstoqueNaoEncontradoException.class, () -> service.buscarPorId(999));
    }

    // ── listar ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("listar: retorna página com todos os estoques")
    void listar_retornaPagina() {
        Page<Estoque> page = new PageImpl<>(List.of(estoque));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Estoque> resultado = service.listar(Pageable.unpaged());

        assertEquals(1, resultado.getTotalElements());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Estoque buildRequest(BigDecimal quantidade) {
        Estoque req = new Estoque();
        req.setProduto(produto);
        req.setLocalizacao("Depósito A");
        req.setQuantidadeTotal(quantidade);
        return req;
    }
}
