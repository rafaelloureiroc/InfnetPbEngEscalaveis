package com.infnet.infnetPB.controller;

import com.infnet.infnetPB.model.history.MesaHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.infnet.infnetPB.DTO.MesaDTO;
import com.infnet.infnetPB.service.MesaService;

import java.util.List;

@RestController
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @PostMapping
    public ResponseEntity<MesaDTO> createMesa(@RequestBody MesaDTO mesaDTO) {
        MesaDTO createdMesa = mesaService.createMesa(mesaDTO);
        return new ResponseEntity<>(createdMesa, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MesaDTO>> getAllMesas() {
        List<MesaDTO> mesas = mesaService.getAllMesas();
        return new ResponseEntity<>(mesas, HttpStatus.OK);
    }
    @GetMapping("/historico")
    public ResponseEntity<List<MesaHistory>> getAllMesaHistories() {
        List<MesaHistory> history = mesaService.getAllMesaHistories();
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}