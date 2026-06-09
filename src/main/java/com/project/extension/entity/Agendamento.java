package com.project.extension.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE agendamento SET ativo = FALSE WHERE id = ?")
@SQLRestriction("ativo = true")
public class Agendamento extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoAgendamento tipoAgendamento;

    private LocalDate dataAgendamento;

    @Column(name = "inicio_agendamento")
    private LocalTime inicioAgendamento;

    @Column(name = "fim_agendamento")
    private LocalTime fimAgendamento;

    private String observacao;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status statusAgendamento;

    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @ManyToOne
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @BatchSize(size = 25)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "agendamento_funcionario",
            joinColumns = @JoinColumn(name = "agendamento_id"),
            inverseJoinColumns = @JoinColumn(name = "funcionario_id")
    )
    private List<Funcionario> funcionarios = new ArrayList<>();

    @BatchSize(size = 25)
    @OneToMany(mappedBy = "agendamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AgendamentoProduto> agendamentoProdutos = new ArrayList<>();

    public Agendamento(TipoAgendamento tipoAgendamento, LocalDate dataAgendamento,
                        LocalTime inicioAgendamento,  LocalTime fimAgendamento,
                        String observacao) {
        this.tipoAgendamento = tipoAgendamento;
        this.dataAgendamento = dataAgendamento;
        this.inicioAgendamento = inicioAgendamento;
        this.fimAgendamento = fimAgendamento;
        this.observacao = observacao;
    }
}
