package com.infnet.infnetPB.controller;

import com.infnet.infnetPB.DTO.PedidoDTO;
import com.infnet.infnetPB.model.history.PedidoHistory;
import com.infnet.infnetPB.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {


    @Autowired
    private  PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoDTO> createPedido(@RequestBody PedidoDTO pedidoDTO) {
        PedidoDTO createdPedido = pedidoService.createPedido(pedidoDTO);
        return new ResponseEntity<>(createdPedido, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> getAllPedidos() {
        List<PedidoDTO> pedidos = pedidoService.getAllPedidos();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedidoById(@PathVariable UUID id) {
        pedidoService.deletePedidoById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> updatePedido(@PathVariable UUID id, @RequestBody PedidoDTO pedidoDTO) {
        PedidoDTO updatedPedido = pedidoService.updatePedido(id, pedidoDTO);
        return new ResponseEntity<>(updatedPedido, HttpStatus.OK);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<PedidoHistory>> getAllPedidoHistory() {
        List<PedidoHistory> history = pedidoService.getAllPedidoHistories();
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}