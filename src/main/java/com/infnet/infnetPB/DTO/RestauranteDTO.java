package com.infnet.infnetPB.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class RestauranteDTO {

    private UUID id;
    private String nome;
    private String localizacao;
}