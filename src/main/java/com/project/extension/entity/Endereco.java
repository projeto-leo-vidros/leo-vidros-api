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
public class Endereco {

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
        this.cep = normalizarCep(cep);
        this.cidade = cidade;
        this.bairro = bairro;
        this.uf = uf;
        this.pais = pais;
        this.numero = numero;
    }

    /**
     * Setter customizado (o Lombok não gera setter quando já existe um) que normaliza o CEP,
     * removendo máscara e qualquer caractere não numérico. Evita o erro de truncamento
     * "Data too long for column 'cep'" quando o cliente envia o CEP formatado (ex: 12345-678).
     */
    public void setCep(String cep) {
        this.cep = normalizarCep(cep);
    }

    private static String normalizarCep(String cep) {
        return cep == null ? null : cep.replaceAll("\\D", "");
    }
}
