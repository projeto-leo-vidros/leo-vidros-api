package com.project.extension.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pedido extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;
    private Boolean ativo;
    private String observacao;

    @Column(name = "forma_pagamento")
    private String formaPagamento;
    private String tipoPedido;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @BatchSize(size = 25)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itensPedido = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Servico servico;

    public Pedido(BigDecimal valorTotal, Boolean ativo, String observacao, String formaPagamento, String tipoPedido) {
        this.valorTotal = valorTotal;
        this.ativo = ativo;
        this.observacao = observacao;
        this.formaPagamento = formaPagamento;
        this.tipoPedido = tipoPedido;
    }
}
