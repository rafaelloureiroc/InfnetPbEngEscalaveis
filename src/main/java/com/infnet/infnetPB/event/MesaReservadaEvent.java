package com.infnet.infnetPB.event;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class MesaReservadaEvent implements Serializable {
    private UUID reservaId;
    private UUID mesaId;
    private LocalDate dataReserva;

    public MesaReservadaEvent(UUID reservaId, UUID mesaId, LocalDate dataReserva) {
        this.reservaId = reservaId;
        this.mesaId = mesaId;
        this.dataReserva = dataReserva;
    }

}