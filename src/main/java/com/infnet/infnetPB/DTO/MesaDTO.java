package com.infnet.infnetPB.DTO;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MesaDTO {

    private UUID id;
    private int qtdAssentosMax;
    private String infoAdicional;
    private UUID restauranteId;
    private UUID reservaId;
    private String NomeRestaurante;
    private List<String> pedidos;
    private String status;


}