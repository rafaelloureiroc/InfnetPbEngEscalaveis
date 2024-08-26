package com.infnet.infnetPB.event;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class MesaCadastradaEvent implements Serializable {
    private UUID mesaId;
    private UUID restauranteId;
    private int qtdAssentosMax;
    private String infoAdicional;
    private String status;

    public MesaCadastradaEvent(UUID mesaId, UUID restauranteId, int qtdAssentosMax, String infoAdicional, String status) {
        this.mesaId = mesaId;
        this.restauranteId = restauranteId;
        this.qtdAssentosMax = qtdAssentosMax;
        this.infoAdicional = infoAdicional;
        this.status = status;
    }
}