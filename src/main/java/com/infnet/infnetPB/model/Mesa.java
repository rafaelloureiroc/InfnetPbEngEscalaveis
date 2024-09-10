package com.infnet.infnetPB.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Mesa {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private int qtdAssentosMax;
    private String infoAdicional;
    private String status;

    @ManyToOne
    private Restaurante restaurante;

    @OneToOne
    private Reserva reserva;

    @OneToOne(mappedBy = "mesa")
    private Pedido pedido;


}