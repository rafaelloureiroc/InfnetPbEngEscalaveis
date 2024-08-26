package com.infnet.infnetPB.event;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class PedidoCriadoEvent implements Serializable {
    private UUID pedidoId;
    private String descricaoPedido;
    private double valorTotal;
    private UUID mesaId;
    private UUID restauranteId;

    public PedidoCriadoEvent(UUID pedidoId, String descricaoPedido, double valorTotal, UUID mesaId, UUID restauranteId) {
        this.pedidoId = pedidoId;
        this.descricaoPedido = descricaoPedido;
        this.valorTotal = valorTotal;
        this.mesaId = mesaId;
        this.restauranteId = restauranteId;
    }
}