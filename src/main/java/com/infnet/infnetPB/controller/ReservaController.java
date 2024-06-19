package com.infnet.infnetPB.controller;

import com.infnet.infnetPB.DTO.ReservaDTO;
import com.infnet.infnetPB.model.history.ReservaHistory;
import com.infnet.infnetPB.model.history.RestauranteHistory;
import com.infnet.infnetPB.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private  ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaDTO> createReserva(@RequestBody ReservaDTO reservaDTO) {
        ReservaDTO createdReserva = reservaService.createReserva(reservaDTO);
        return new ResponseEntity<>(createdReserva, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> getAllReservas() {
        List<ReservaDTO> reservas = reservaService.getAllReservas();
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservaById(@PathVariable UUID id) {
        reservaService.deleteReservaById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<ReservaHistory>> getAllReservaHistories() {
        List<ReservaHistory> history = reservaService.getAllReservaHistories();
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}
