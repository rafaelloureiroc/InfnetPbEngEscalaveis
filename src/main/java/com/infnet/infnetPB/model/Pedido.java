package com.infnet.infnetPB.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Pedido {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String descricaoPedido;
    private double valorTotal;

    @ManyToOne
    private Restaurante restaurante;

    @OneToOne
    private Mesa mesa;

}