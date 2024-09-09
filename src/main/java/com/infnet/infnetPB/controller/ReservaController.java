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
        try {
            ReservaDTO createdReserva = reservaService.createReserva(reservaDTO);
            return new ResponseEntity<>(createdReserva, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> getAllReservas() {
        List<ReservaDTO> reservas = reservaService.getAllReservas();
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> getReservaById(@PathVariable UUID id) {
        try {
            ReservaDTO reservaDTO = reservaService.getReservaById(id);
            return new ResponseEntity<>(reservaDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaDTO> updateReserva(@PathVariable UUID id, @RequestBody ReservaDTO reservaDTO) {
        try {
            ReservaDTO updatedReserva = reservaService.updateReserva(id, reservaDTO);
            return new ResponseEntity<>(updatedReserva, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservaById(@PathVariable UUID id) {
        try {
            reservaService.deleteReservaById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/historico")
    public ResponseEntity<List<ReservaHistory>> getAllReservaHistories() {
        List<ReservaHistory> history = reservaService.getAllReservaHistories();
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}