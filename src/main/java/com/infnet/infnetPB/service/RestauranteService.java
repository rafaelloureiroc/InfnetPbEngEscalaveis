package com.infnet.infnetPB.service;

import com.infnet.infnetPB.DTO.RestauranteDTO;
import com.infnet.infnetPB.model.Restaurante;
import com.infnet.infnetPB.model.history.RestauranteHistory;
import com.infnet.infnetPB.repository.RestauranteRepository;
import com.infnet.infnetPB.repository.historyRepository.RestauranteHistoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RestauranteService {

    @Autowired
    private  RestauranteRepository restauranteRepository;

    @Autowired
    private RestauranteHistoryRepository restauranteHistoryRepository;

    @Transactional
    public RestauranteDTO createRestaurante(RestauranteDTO restauranteDTO) {
        Restaurante restaurante = new Restaurante();
        BeanUtils.copyProperties(restauranteDTO, restaurante);
        Restaurante savedRestaurante = restauranteRepository.save(restaurante);
        saveRestauranteHistory(savedRestaurante, "CREATE");
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
        history.setLocalizacao(restaurante.getLocalizacao());
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
