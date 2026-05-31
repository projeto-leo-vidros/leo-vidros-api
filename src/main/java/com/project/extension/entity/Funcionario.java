package com.project.extension.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Funcionario extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String telefone;
    private String funcao;
    private String contrato;
    private String escala;
    private Boolean ativo;

    @JsonIgnore
    @ManyToMany(mappedBy = "funcionarios")
    private List<Agendamento> agendamentos;

    public Funcionario(String nome, String telefone, String funcao,
                       String contrato, String escala, Boolean ativo) {
        this.nome = nome;
        this.telefone = telefone;
        this.funcao = funcao;
        this.contrato = contrato;
        this.escala = escala;
        this.ativo = ativo;
    }
}
