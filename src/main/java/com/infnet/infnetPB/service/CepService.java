package com.infnet.infnetPB.service;


import com.infnet.infnetPB.model.Cep;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class CepService {
    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<Cep> getCepDetails(String cep) {
        try {
            String cepApiUrl = "https://viacep.com.br/ws/{cep}/json/";
            Cep response = restTemplate.getForObject(cepApiUrl, Cep.class, cep);
            if (response != null && response.getLogradouro() != null) {
                return Optional.of(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}