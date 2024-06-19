package com.infnet.infnetPB.model.history;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infnet.infnetPB.model.Pedido;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class PedidoHistory {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne
    @JsonIgnore
    private Pedido pedido;

    private String descricaoPedido;
    private double valorTotal;
    private LocalDateTime timestamp;
    private String operation;
}
