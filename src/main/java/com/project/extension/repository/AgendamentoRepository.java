package com.project.extension.repository;

import com.project.extension.controller.dashboard.dto.ProximosAgendamentosResponseDto;
import com.project.extension.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

    @Query(
            value = """
                SELECT COUNT(*)
                FROM agendamento a
                JOIN servico s ON s.id = a.servico_id
                LEFT JOIN pedido p ON p.id = s.pedido_id
                LEFT JOIN status ps ON ps.id = p.status_id
                LEFT JOIN status ast ON ast.id = a.status_id
                WHERE DATE(a.data_agendamento) = CURRENT_DATE
                  AND a.inicio_agendamento > CURRENT_TIME
                  AND (p.ativo IS NULL OR p.ativo = TRUE)
                  AND (ps.nome IS NULL OR UPPER(ps.nome) NOT IN ('FINALIZADO', 'INATIVO', 'CONCLUIDO', 'CONCLUÍDO', 'CANCELADO'))
                  AND (ast.nome IS NULL OR UPPER(ast.nome) NOT IN ('CANCELADO', 'CONCLUIDO', 'CONCLUÍDO'))
            """,
            nativeQuery = true
    )
    int countQtdAgendamentosHoje();

    @Query(
            value = """
                SELECT COUNT(*)
                FROM agendamento a
                JOIN servico s ON s.id = a.servico_id
                LEFT JOIN pedido p ON p.id = s.pedido_id
                LEFT JOIN status ps ON ps.id = p.status_id
                LEFT JOIN status ast ON ast.id = a.status_id
                WHERE DATE(a.data_agendamento) = CURRENT_DATE
                  AND a.tipo = 'SERVICO'
                  AND (p.ativo IS NULL OR p.ativo = TRUE)
                  AND (ps.nome IS NULL OR UPPER(ps.nome) NOT IN ('FINALIZADO', 'INATIVO', 'CONCLUIDO', 'CONCLUÍDO', 'CANCELADO'))
                  AND (ast.nome IS NULL OR UPPER(ast.nome) NOT IN ('CANCELADO', 'CONCLUIDO', 'CONCLUÍDO'))
                """,
            nativeQuery = true
    )
    int countServicosHoje();

    @Query(
            value = """
                SELECT COUNT(*)
                FROM agendamento a
                JOIN servico s ON s.id = a.servico_id
                LEFT JOIN pedido p ON p.id = s.pedido_id
                LEFT JOIN status ps ON ps.id = p.status_id
                LEFT JOIN status ast ON ast.id = a.status_id
                WHERE DATE(a.data_agendamento) > CURRENT_DATE
                  AND (p.ativo IS NULL OR p.ativo = TRUE)
                  AND (ps.nome IS NULL OR UPPER(ps.nome) NOT IN ('FINALIZADO', 'INATIVO', 'CONCLUIDO', 'CONCLUÍDO', 'CANCELADO'))
                  AND (ast.nome IS NULL OR UPPER(ast.nome) NOT IN ('CANCELADO', 'CONCLUIDO', 'CONCLUÍDO'))
        """,
            nativeQuery = true
    )
    int countQtdAgendamentosFuturos();

    @Query("""
        SELECT new com.project.extension.controller.dashboard.dto.ProximosAgendamentosResponseDto(
               a.id,
               a.dataAgendamento,
               a.inicioAgendamento,
               a.fimAgendamento,
               s.nome,
               a.observacao,
               CAST(s.precoBase AS java.math.BigDecimal),
               s.pedido.observacao,
               s.ativo,
               st.nome
        )
        FROM Agendamento a
        JOIN a.servico s
        LEFT JOIN s.pedido p
        LEFT JOIN p.status ps
        JOIN a.statusAgendamento st
        WHERE a.dataAgendamento >= CURRENT_DATE
          AND (p IS NULL OR p.ativo = TRUE)
          AND (ps IS NULL OR UPPER(ps.nome) NOT IN ('FINALIZADO', 'INATIVO', 'CONCLUIDO', 'CONCLUÍDO', 'CANCELADO'))
          AND UPPER(st.nome) NOT IN ('CANCELADO', 'CONCLUIDO', 'CONCLUÍDO')
        ORDER BY a.dataAgendamento ASC, a.inicioAgendamento ASC
    """)
    List<ProximosAgendamentosResponseDto> proximosAgendamentos();

    @Query(value = """
        SELECT
            COALESCE(CAST(
                (SUM(TIMESTAMPDIFF(MINUTE, a.inicio_agendamento, a.fim_agendamento)) / 60) /
                (COUNT(DISTINCT af.funcionario_id) * 9) * 100
            AS DECIMAL(5,2)), 0.0) AS taxa_ocupacao_percentual
                FROM agendamento a
                JOIN agendamento_funcionario af ON a.id = af.agendamento_id
                WHERE a.data_agendamento = CURRENT_DATE
        """,
    nativeQuery = true)

    Double taxaOcupacaoServicos();

    @Query("""
        SELECT DISTINCT a FROM Agendamento a
        JOIN FETCH a.funcionarios f
        LEFT JOIN FETCH a.servico s
        LEFT JOIN FETCH a.statusAgendamento
        WHERE f.id = :funcionarioId
        AND a.dataAgendamento BETWEEN :dataInicio AND :dataFim
        ORDER BY a.dataAgendamento ASC, a.inicioAgendamento ASC
    """)
    List<Agendamento> findAgendamentosByFuncionarioAndPeriodo(
            @Param("funcionarioId") Integer funcionarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("""
        SELECT a FROM Agendamento a
        JOIN a.funcionarios f
        WHERE f.id = :funcionarioId
        AND a.dataAgendamento = :data
        AND (:novoInicio < a.fimAgendamento AND :novoFim > a.inicioAgendamento)
    """)
    List<Agendamento> findConflitos(
            @Param("funcionarioId") Integer funcionarioId,
            @Param("data") LocalDate data,
            @Param("novoInicio") LocalTime novoInicio,
            @Param("novoFim") LocalTime novoFim
    );

    @Query("""
        SELECT COUNT(f) FROM Agendamento a
        JOIN a.funcionarios f
        WHERE a.id = :agendamentoId
    """)
    int countFuncionariosByAgendamentoId(@Param("agendamentoId") Integer agendamentoId);

    @Query("""
        SELECT a
        FROM Agendamento a
        JOIN a.funcionarios f
        WHERE f.id = :funcionarioId
          AND a.dataAgendamento >= :hoje
          AND a.statusAgendamento.nome NOT IN ('CANCELADO', 'CONCLUIDO')
        ORDER BY a.dataAgendamento ASC, a.inicioAgendamento ASC
    """)
    List<Agendamento> findAgendamentosFuturosAtivosByFuncionario(
            @Param("funcionarioId") Integer funcionarioId,
            @Param("hoje") LocalDate hoje
    );

    @Query("""
        SELECT a
        FROM Agendamento a
        JOIN FETCH a.servico
        JOIN FETCH a.statusAgendamento
        WHERE a.servico.id = :servicoId
        AND a.statusAgendamento.nome NOT IN ('CANCELADO', 'INATIVO')
        ORDER BY a.dataAgendamento ASC, a.inicioAgendamento ASC
    """)
    List<Agendamento> findAtivosByServicoId(@Param("servicoId") Integer servicoId);

    @Query("""
        SELECT a FROM Agendamento a
        WHERE a.servico.id = :servicoId
        AND a.tipoAgendamento = com.project.extension.entity.TipoAgendamento.SERVICO
        AND a.statusAgendamento.nome NOT IN ('CANCELADO', 'INATIVO')
    """)
    List<Agendamento> findAgendamentosServicoAtivosByServico(@Param("servicoId") Integer servicoId);
}
