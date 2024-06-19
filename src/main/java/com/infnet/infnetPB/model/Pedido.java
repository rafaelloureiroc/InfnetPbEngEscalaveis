package com.infnet.infnetPB.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    private Mesa mesa;

}