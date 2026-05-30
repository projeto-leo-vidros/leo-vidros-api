package com.project.extension.repository;

import com.project.extension.entity.Estoque;
import com.project.extension.entity.Produto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Estoque e WHERE e.produto = :produto AND e.localizacao = :localizacao")
    Optional<Estoque> findByProdutoAndLocalizacaoForUpdate(@Param("produto") Produto produto, @Param("localizacao") String localizacao);

    Optional<Estoque> findByProdutoAndLocalizacao(Produto produto, String localizacao);

    Optional<Estoque> findByProduto(Produto produto);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Estoque e WHERE e.produto.id = :produtoId")
    Optional<Estoque> findByProdutoIdForUpdate(@Param("produtoId") Integer produtoId);

    @Query(
        value = """
        SELECT COUNT(*)
        FROM estoque e
        JOIN produto p ON p.id = e.produto_id
        JOIN metrica_estoque m ON m.id = p.metrica_estoque_id
        WHERE e.quantidade_disponivel < m.nivel_minimo
        """,
        nativeQuery = true
    )
    int countItensAbaixoMinimo();

    @Query(
            value = """
        SELECT
        e.id,
        e.quantidade_total,
        e.quantidade_disponivel,
        e.reservado,
        e.localizacao,
        p.nome,
        p.descricao,
        p.unidade_medida,
        p.preco,
        m.nivel_minimo,
        m.nivel_maximo
        FROM estoque e
        JOIN produto p ON p.id = e.produto_id
        JOIN metrica_estoque m ON m.id = p.metrica_estoque_id
        WHERE e.quantidade_disponivel < m.nivel_minimo
        """,
            nativeQuery = true
    )
    List<Object[]> estoqueCriticoRaw();

    Optional<Estoque> findByProdutoId(Integer id);
}
