package com.project.extension.repository;

import com.project.extension.entity.Etapa;
import com.project.extension.entity.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    @EntityGraph(attributePaths = {"status", "cliente"})
    Page<Pedido> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"status", "cliente"})
    Page<Pedido> findAllByServico_Etapa(Etapa etapa, Pageable pageable);

    @EntityGraph(attributePaths = {"status", "cliente"})
    Page<Pedido> findByTipoPedidoIgnoreCase(String tipo, Pageable pageable);

    @EntityGraph(attributePaths = {"status", "cliente"})
    Page<Pedido> findByServicoIsNotNull(Pageable pageable);

    List<Pedido> findByServicoIsNotNull();

    @EntityGraph(attributePaths = {"status", "cliente"})
    Page<Pedido> findByTipoPedidoIgnoreCaseAndItensPedidoIsNotEmpty(String tipo, Pageable pageable);

    @Query(value = """
        SELECT COALESCE(SUM(valor_total), 0)
        FROM pedido
        WHERE MONTH(created_at) = MONTH(CURRENT_DATE)
          AND YEAR(created_at) = YEAR(CURRENT_DATE)
          AND ativo = true
    """, nativeQuery = true)
    BigDecimal sumFaturamentoMesAtual();

    @Query(value = """
        SELECT COALESCE(SUM(valor_total), 0)
        FROM pedido
        WHERE MONTH(created_at) = MONTH(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH))
          AND YEAR(created_at) = YEAR(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH))
          AND ativo = true
    """, nativeQuery = true)
    BigDecimal sumFaturamentoMesAnterior();

    @Query(value = """
        SELECT MONTH(created_at) as mes, COALESCE(SUM(valor_total), 0) as valor
        FROM pedido
        WHERE YEAR(created_at) = YEAR(CURRENT_DATE)
          AND ativo = true
        GROUP BY MONTH(created_at)
        ORDER BY mes
    """, nativeQuery = true)
    List<Object[]> sumFaturamentoPorMesAnoAtual();

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE pedido p
        INNER JOIN servico s ON s.pedido_id = p.id
        INNER JOIN etapa e   ON e.id = s.etapa_id
        INNER JOIN status st ON st.tipo = 'PEDIDO' AND st.nome = 'INATIVO'
        SET p.ativo     = FALSE,
            p.status_id = st.id,
            s.ativo     = FALSE
        WHERE e.tipo = 'PEDIDO'
          AND e.nome IN ('CONCLUÍDO', 'CONCLUIDO')
          AND p.ativo = TRUE
    """, nativeQuery = true)
    int finalizarPedidosConcluidos();

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE pedido p
        INNER JOIN servico s   ON s.pedido_id = p.id
        INNER JOIN agendamento a ON a.servico_id = s.id AND a.tipo = 'SERVICO'
        INNER JOIN status ag_st ON ag_st.id = a.status_id AND ag_st.nome IN ('CONCLUÍDO', 'CONCLUIDO')
        INNER JOIN status p_st  ON p_st.tipo = 'PEDIDO' AND p_st.nome = 'INATIVO'
        INNER JOIN etapa e_con  ON e_con.tipo = 'PEDIDO' AND e_con.nome = 'CONCLUÍDO'
        SET p.ativo     = FALSE,
            p.status_id = p_st.id,
            s.etapa_id  = e_con.id,
            s.ativo     = FALSE
        WHERE p.ativo = TRUE
    """, nativeQuery = true)
    int finalizarPedidosComAgendamentoConcluido();
}
