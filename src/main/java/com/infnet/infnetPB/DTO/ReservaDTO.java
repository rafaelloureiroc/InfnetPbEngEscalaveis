package com.infnet.infnetPB.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ReservaDTO {

    private UUID id;
    private LocalDate dataReserva;
    private int quantidadePessoas;
    private UUID mesaId;
    private UUID restauranteId;

}