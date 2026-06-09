package com.project.extension.repository;

import com.project.extension.entity.ServicoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ServicoProdutoRepository extends JpaRepository<ServicoProduto, Integer> {

    List<ServicoProduto> findByServicoIdAndAtivoTrueOrderByOrdemAscIdAsc(Integer servicoId);

    List<ServicoProduto> findByServicoIdOrderByOrdemAscIdAsc(Integer servicoId);

    Optional<ServicoProduto> findByServicoIdAndProdutoId(Integer servicoId, Integer produtoId);

    /**
     * Soma da quantidade planejada (reservada) de um produto considerando apenas serviços
     * que possuem um agendamento de SERVIÇO ativo (não cancelado/concluído). Estimativa e
     * vistoria (orçamento) não reservam estoque.
     */
    @Query("""
            SELECT COALESCE(SUM(sp.quantidadePlanejada), 0)
            FROM ServicoProduto sp
            JOIN sp.servico s
            WHERE sp.produto.id = :produtoId
              AND sp.ativo = true
              AND EXISTS (
                    SELECT 1
                    FROM Agendamento a
                    JOIN a.statusAgendamento st
                    WHERE a.servico = s
                      AND a.tipoAgendamento = com.project.extension.entity.TipoAgendamento.SERVICO
                      AND st.nome NOT IN ('CANCELADO', 'CONCLUIDO', 'CONCLUÍDO', 'INATIVO')
              )
            """)
    BigDecimal somarReservasAtivasServicoPorProdutoId(@Param("produtoId") Integer produtoId);
}
