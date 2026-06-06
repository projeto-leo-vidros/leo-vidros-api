package com.project.extension.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "servico_produto")
public class ServicoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "quantidade_planejada", precision = 18, scale = 5, nullable = false)
    private BigDecimal quantidadePlanejada = BigDecimal.ZERO;

    @Column(name = "quantidade_utilizada", precision = 18, scale = 5)
    private BigDecimal quantidadeUtilizada;

    @Column(name = "preco_unitario", precision = 18, scale = 5, nullable = false)
    private BigDecimal precoUnitario = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column
    private Integer ordem = 0;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}
