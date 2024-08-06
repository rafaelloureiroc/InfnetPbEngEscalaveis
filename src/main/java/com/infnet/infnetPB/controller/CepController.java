package com.infnet.infnetPB.controller;

import com.infnet.infnetPB.model.Cep;
import com.infnet.infnetPB.service.CepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/ceps")
public class CepController {

    @Autowired
    private CepService cepService;

    @GetMapping("/{cep}")
    public ResponseEntity<Cep> getCepDetails(@PathVariable String cep) {
        Optional<Cep> cepOptional = cepService.getCepDetails(cep);
        return cepOptional.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
