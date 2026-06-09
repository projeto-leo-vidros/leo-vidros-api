package com.project.extension.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String email;
    @Column(columnDefinition = "CHAR(11)")
    private String cpf;
    private String telefone;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;
}
