package com.infnet.infnetPB.service;

import com.infnet.infnetPB.DTO.RestauranteDTO;
import com.infnet.infnetPB.InfnetPbApplication;
import com.infnet.infnetPB.event.RestauranteCadastradoEvent;
import com.infnet.infnetPB.model.Cep;
import com.infnet.infnetPB.model.Restaurante;
import com.infnet.infnetPB.model.history.RestauranteHistory;
import com.infnet.infnetPB.repository.RestauranteRepository;
import com.infnet.infnetPB.repository.historyRepository.RestauranteHistoryRepository;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private RestauranteHistoryRepository restauranteHistoryRepository;

    @Autowired
    private CepService cepService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final static Logger logger = Logger.getLogger(RestauranteService.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Transactional
    public RestauranteDTO createRestaurante(RestauranteDTO restauranteDTO) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(restauranteDTO.getNome());
        restaurante.setCep(restauranteDTO.getCep());

        Optional<Cep> cepDetails = cepService.getCepDetails(restauranteDTO.getCep());
        if (cepDetails.isPresent()) {
            Cep cep = cepDetails.get();
            restaurante.setLogradouro(cep.getLogradouro());
            restaurante.setBairro(cep.getBairro());
            restaurante.setCidade(cep.getLocalidade());
            restaurante.setUf(cep.getUf());
        } else {
            logger.warn("CEP não encontrado ou inválido: " + restauranteDTO.getCep());
        }

        Restaurante savedRestaurante = restauranteRepository.save(restaurante);
        saveRestauranteHistory(savedRestaurante, "CREATE");

        RestauranteCadastradoEvent event = new RestauranteCadastradoEvent(
                savedRestaurante.getId(),
                savedRestaurante.getNome(),
                savedRestaurante.getCep(),
                savedRestaurante.getLogradouro(),
                savedRestaurante.getBairro(),
                savedRestaurante.getCidade(),
                savedRestaurante.getUf()
        );

        logger.info("Tentando enviar evento restauranteCadastrado: " + event);

        CompletableFuture.runAsync(() -> {
            boolean success = sendEventWithRetry(event, "restauranteExchange", "restauranteCadastrado");
            if (success) {
                logger.info("Evento restauranteCadastrado enviado com sucesso.");
            } else {
                logger.error("Falha ao enviar evento restauranteCadastrado após " + MAX_RETRIES + " tentativas.");
            }
        });

        return convertToDTO(savedRestaurante);
    }

    private boolean sendEventWithRetry(Object event, String exchange, String routingKey) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                rabbitTemplate.convertAndSend(exchange, routingKey, event);
                return true;
            } catch (Exception e) {
                logger.error("Erro ao enviar evento (tentativa " + attempt + "): " + e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return false;
    }

    @Transactional
    public List<RestauranteDTO> getAllRestaurantes() {
        List<Restaurante> restaurantes = restauranteRepository.findAll();
        return restaurantes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRestauranteById(UUID id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        restauranteRepository.deleteById(id);
        saveRestauranteHistory(restaurante, "DELETE");
    }

    @Transactional
    public RestauranteDTO updateRestaurante(UUID id, RestauranteDTO restauranteDTO) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        restaurante.setNome(restauranteDTO.getNome());
        restaurante.setCep(restauranteDTO.getCep());

        Optional<Cep> cepDetails = cepService.getCepDetails(restauranteDTO.getCep());
        if (cepDetails.isPresent()) {
            Cep cep = cepDetails.get();
            restaurante.setLogradouro(cep.getLogradouro());
            restaurante.setBairro(cep.getBairro());
            restaurante.setCidade(cep.getLocalidade());
            restaurante.setUf(cep.getUf());
        } else {
            logger.warn("CEP não encontrado ou inválido: " + restauranteDTO.getCep());
        }

        Restaurante updatedRestaurante = restauranteRepository.save(restaurante);
        saveRestauranteHistory(updatedRestaurante, "UPDATE");

        return convertToDTO(updatedRestaurante);
    }


    public List<RestauranteHistory> getAllRestauranteHistories() {
        return restauranteHistoryRepository.findAll();
    }

    private void saveRestauranteHistory(Restaurante restaurante, String operation) {
        RestauranteHistory history = new RestauranteHistory();
        history.setRestaurante(restaurante);
        history.setNome(restaurante.getNome());
        history.setCep(restaurante.getCep());
        history.setLogradouro(restaurante.getLogradouro());
        history.setBairro(restaurante.getBairro());
        history.setCidade(restaurante.getCidade());
        history.setUf(restaurante.getUf());
        history.setTimestamp(LocalDateTime.now());
        history.setOperation(operation);
        restauranteHistoryRepository.save(history);
    }

    private RestauranteDTO convertToDTO(Restaurante restaurante) {
        RestauranteDTO restauranteDTO = new RestauranteDTO();
        BeanUtils.copyProperties(restaurante, restauranteDTO);
        return restauranteDTO;
    }
}
