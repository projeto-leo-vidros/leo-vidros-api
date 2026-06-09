package com.project.extension.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Usuario extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String email;
    @Column(columnDefinition = "CHAR(11)")
    private String cpf;
    private String senha;
    private String telefone;

    @Column(name = "first_login")
    private Boolean firstLogin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    public Usuario(String nome, String email, String cpf, String senha, String telefone, Boolean firstLogin) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.senha = senha;
        this.telefone = telefone;
        this.firstLogin = firstLogin;
    }
}
