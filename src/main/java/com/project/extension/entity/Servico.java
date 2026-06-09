package com.project.extension.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String codigo;
    private String descricao;

    @Column(name = "preco_base")
    private BigDecimal precoBase;

    private Boolean ativo;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "etapa_id")
    private Etapa etapa;

    @OneToOne
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL)
    private List<Agendamento> agendamentos = new ArrayList<>();

    public Servico(String nome, String descricao, BigDecimal precoBase, Boolean ativo) {
        this.nome = nome;
        this.descricao = descricao;
        this.precoBase = precoBase;
        this.ativo = ativo;
    }
}
