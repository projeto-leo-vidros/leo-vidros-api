package com.project.extension.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Produto extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String descricao;

    @Column(name = "unidade_medida")
    private String unidademedida;

    private BigDecimal preco;
    private Boolean ativo;

    @OneToMany(mappedBy = "produto")
    private List<AtributoProduto> atributos;

    @OneToMany(mappedBy = "produto")
    private List<AgendamentoProduto> agendamentoProdutos;

    @OneToOne(cascade = CascadeType.ALL)
    private MetricaEstoque metricaEstoque;

    public Produto(String nome, String descricao, String unidademedida, BigDecimal preco, Boolean ativo) {
        this.nome = nome;
        this.descricao = descricao;
        this.unidademedida = unidademedida;
        this.preco = preco;
        this.ativo = ativo;
    }
}
