package com.infnet.infnetPB.controller;

import com.infnet.infnetPB.DTO.RestauranteDTO;
import com.infnet.infnetPB.model.history.MesaHistory;
import com.infnet.infnetPB.model.history.RestauranteHistory;
import com.infnet.infnetPB.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

    @Autowired
    private  RestauranteService restauranteService;

    @PostMapping
    public ResponseEntity<RestauranteDTO> createRestaurante(@RequestBody RestauranteDTO restauranteDTO) {
        RestauranteDTO createdRestaurante = restauranteService.createRestaurante(restauranteDTO);
        return new ResponseEntity<>(createdRestaurante, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RestauranteDTO>> getAllRestaurantes() {
        List<RestauranteDTO> restaurantes = restauranteService.getAllRestaurantes();
        return new ResponseEntity<>(restaurantes, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestauranteById(@PathVariable UUID id) {
        restauranteService.deleteRestauranteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<RestauranteHistory>> getAllRestauranteHistories() {
        List<RestauranteHistory> history = restauranteService.getAllRestauranteHistories();
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}
