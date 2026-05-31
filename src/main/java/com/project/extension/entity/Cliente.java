package com.project.extension.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE cliente SET status = 'INATIVO' WHERE id = ?")
@SQLRestriction("status <> 'INATIVO'")
public class Cliente extends BaseEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String status;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "cliente_endereco",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "endereco_id")
    )
    private List<Endereco> enderecos = new ArrayList<>();

    public Cliente(String nome, String cpf, String email, String telefone, String status) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.status = status;
    }
}
