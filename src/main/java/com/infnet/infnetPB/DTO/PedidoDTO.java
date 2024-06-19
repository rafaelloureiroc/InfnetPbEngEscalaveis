package com.infnet.infnetPB.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class PedidoDTO {

    private UUID id;
    private String descricaoPedido;
    private double valorTotal;
    private UUID restauranteId;
    private UUID mesaId;
}