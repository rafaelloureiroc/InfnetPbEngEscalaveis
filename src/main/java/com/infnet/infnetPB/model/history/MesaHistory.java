package com.infnet.infnetPB.model.history;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infnet.infnetPB.model.Mesa;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity

public class MesaHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JsonIgnore
    private Mesa mesa;

    private Integer qtdAssentosMax;
    private String infoAdicional;
    private String status;
    private LocalDateTime timestamp;
    private String operation;
}
