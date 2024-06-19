package com.infnet.infnetPB.model.history;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infnet.infnetPB.model.Reserva;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class ReservaHistory {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne
    @JsonIgnore
    private Reserva reserva;

    private LocalDateTime dataReserva;
    private int quantidadePessoas;
    private LocalDateTime timestamp;
    private String operation;
}
