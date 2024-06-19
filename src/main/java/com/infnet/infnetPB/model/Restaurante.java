package com.infnet.infnetPB.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Restaurante{

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String nome;
    private String localizacao;

}
