package com.project.extension.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Endereco extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String rua;
    private String complemento;
    private String cep;
    private String cidade;
    private String bairro;
    private String uf;
    private String pais;
    private Integer numero;

    public Endereco(String rua, String complemento, String cep, String cidade, String bairro, String uf, String pais, Integer numero) {
        this.rua = rua;
        this.complemento = complemento;
        this.cep = cep;
        this.cidade = cidade;
        this.bairro = bairro;
        this.uf = uf;
        this.pais = pais;
        this.numero = numero;
    }
}
