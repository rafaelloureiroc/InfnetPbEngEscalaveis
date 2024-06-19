package com.infnet.infnetPB.model.history;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infnet.infnetPB.model.Restaurante;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class RestauranteHistory {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne
    @JsonIgnore
    private Restaurante restaurante;

    private String nome;
    private String localizacao;
    private LocalDateTime timestamp;
    private String operation;
}