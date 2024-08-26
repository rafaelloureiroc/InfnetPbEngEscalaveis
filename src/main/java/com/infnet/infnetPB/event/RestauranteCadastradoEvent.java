package com.infnet.infnetPB.event;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class RestauranteCadastradoEvent implements Serializable {
    private UUID restauranteId;
    private String nome;
    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;

    public RestauranteCadastradoEvent(UUID restauranteId, String nome, String cep, String logradouro, String bairro, String cidade, String uf) {
        this.restauranteId = restauranteId;
        this.nome = nome;
        this.cep = cep;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
    }
}