package com.infnet.infnetPB.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
public class Reserva {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private LocalDate dataReserva;
    private int quantidadePessoas;

    @ManyToOne
    private Mesa mesa;

    @ManyToOne
    private Restaurante restaurante;

}