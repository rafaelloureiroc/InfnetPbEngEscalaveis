package com.infnet.infnetPB.service;

import com.infnet.infnetPB.DTO.RestauranteDTO;
import com.infnet.infnetPB.event.RestauranteCadastradoEvent;
import com.infnet.infnetPB.model.Cep;
import com.infnet.infnetPB.model.Restaurante;
import com.infnet.infnetPB.model.history.RestauranteHistory;
import com.infnet.infnetPB.repository.RestauranteRepository;
import com.infnet.infnetPB.repository.historyRepository.RestauranteHistoryRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RestauranteService {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteService.class);

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private RestauranteHistoryRepository restauranteHistoryRepository;

    @Autowired
    private CepService cepService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
            logger.warn("CEP não encontrado ou inválido: {}", restauranteDTO.getCep());
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
        try{
            rabbitTemplate.convertAndSend("restauranteExchange", "restauranteCadastrado", event);
        } catch (Exception e){
            System.err.println("(Restaurante) Falha ao comunicar com RabbitMQ: " + e.getMessage());
        }

        return convertToDTO(savedRestaurante);
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
        restauranteRepository.deleteById(id);
    }

    public List<RestauranteHistory> getAllRestauranteHistories() {
        return restauranteHistoryRepository.findAll();
    }

    private void saveRestauranteHistory(Restaurante restaurante, String operation) {
        RestauranteHistory history = new RestauranteHistory();
        history.setRestaurante(restaurante);
        history.setNome(restaurante.getNome());
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
