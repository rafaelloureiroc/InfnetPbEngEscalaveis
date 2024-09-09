package com.infnet.infnetPB.controller;

import com.infnet.infnetPB.DTO.RestauranteDTO;
import com.infnet.infnetPB.model.history.RestauranteHistory;
import com.infnet.infnetPB.service.RestauranteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RestauranteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RestauranteService restauranteService;

    @InjectMocks
    private RestauranteController restauranteController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(restauranteController).build();
    }

    @Test
    public void testCreateRestaurante() throws Exception {
        RestauranteDTO restauranteDTO = new RestauranteDTO();
        UUID restauranteId = UUID.randomUUID();
        restauranteDTO.setId(restauranteId);
        restauranteDTO.setNome("Restaurante 1");
        restauranteDTO.setCep("24355210");
        restauranteDTO.setLogradouro("Rua A");
        restauranteDTO.setBairro("Bairro B");
        restauranteDTO.setCidade("Cidade C");
        restauranteDTO.setUf("UF");

        when(restauranteService.createRestaurante(any(RestauranteDTO.class))).thenReturn(restauranteDTO);

        mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + restauranteId + "\",\"nome\":\"Restaurante 1\",\"cep\":\"24355210\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(restauranteId.toString()))
                .andExpect(jsonPath("$.nome").value("Restaurante 1"))
                .andExpect(jsonPath("$.cep").value("24355210"))
                .andExpect(jsonPath("$.logradouro").value("Rua A"))
                .andExpect(jsonPath("$.bairro").value("Bairro B"))
                .andExpect(jsonPath("$.cidade").value("Cidade C"))
                .andExpect(jsonPath("$.uf").value("UF"));
    }

    @Test
    public void testGetAllRestaurantes() throws Exception {
        RestauranteDTO restaurante1 = new RestauranteDTO();
        UUID restauranteId1 = UUID.randomUUID();
        restaurante1.setId(restauranteId1);
        restaurante1.setNome("Restaurante 1");
        restaurante1.setCep("24355210");
        restaurante1.setLogradouro("Rua A");
        restaurante1.setBairro("Bairro B");
        restaurante1.setCidade("Cidade C");
        restaurante1.setUf("UF");

        RestauranteDTO restaurante2 = new RestauranteDTO();
        UUID restauranteId2 = UUID.randomUUID();
        restaurante2.setId(restauranteId2);
        restaurante2.setNome("Restaurante 2");
        restaurante2.setCep("24355211");
        restaurante2.setLogradouro("Rua D");
        restaurante2.setBairro("Bairro E");
        restaurante2.setCidade("Cidade F");
        restaurante2.setUf("UF");

        List<RestauranteDTO> restauranteList = Arrays.asList(restaurante1, restaurante2);

        when(restauranteService.getAllRestaurantes()).thenReturn(restauranteList);

        mockMvc.perform(get("/restaurantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(restauranteId1.toString()))
                .andExpect(jsonPath("$[0].nome").value("Restaurante 1"))
                .andExpect(jsonPath("$[0].cep").value("24355210"))
                .andExpect(jsonPath("$[0].logradouro").value("Rua A"))
                .andExpect(jsonPath("$[0].bairro").value("Bairro B"))
                .andExpect(jsonPath("$[0].cidade").value("Cidade C"))
                .andExpect(jsonPath("$[0].uf").value("UF"))
                .andExpect(jsonPath("$[1].id").value(restauranteId2.toString()))
                .andExpect(jsonPath("$[1].nome").value("Restaurante 2"))
                .andExpect(jsonPath("$[1].cep").value("24355211"))
                .andExpect(jsonPath("$[1].logradouro").value("Rua D"))
                .andExpect(jsonPath("$[1].bairro").value("Bairro E"))
                .andExpect(jsonPath("$[1].cidade").value("Cidade F"))
                .andExpect(jsonPath("$[1].uf").value("UF"));
    }

    @Test
    public void testDeleteRestauranteById() throws Exception {
        UUID restauranteId = UUID.randomUUID();
        doNothing().when(restauranteService).deleteRestauranteById(restauranteId);

        mockMvc.perform(delete("/restaurantes/" + restauranteId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAllRestauranteHistories() throws Exception {
        RestauranteHistory history1 = new RestauranteHistory();
        UUID historyId1 = UUID.randomUUID();
        history1.setId(historyId1);

        RestauranteHistory history2 = new RestauranteHistory();
        UUID historyId2 = UUID.randomUUID();
        history2.setId(historyId2);

        List<RestauranteHistory> historyList = Arrays.asList(history1, history2);

        when(restauranteService.getAllRestauranteHistories()).thenReturn(historyList);

        mockMvc.perform(get("/restaurantes/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(historyId1.toString()))
                .andExpect(jsonPath("$[1].id").value(historyId2.toString()));
    }
    @Test
    public void testUpdateRestaurante() throws Exception {
        UUID restauranteId = UUID.randomUUID();

        RestauranteDTO restauranteDTO = new RestauranteDTO();
        restauranteDTO.setId(restauranteId);
        restauranteDTO.setNome("Restaurante Atualizado");
        restauranteDTO.setCep("24355220");
        restauranteDTO.setLogradouro("Rua B");
        restauranteDTO.setBairro("Bairro C");
        restauranteDTO.setCidade("Cidade D");
        restauranteDTO.setUf("UF");

        when(restauranteService.updateRestaurante(any(UUID.class), any(RestauranteDTO.class))).thenReturn(restauranteDTO);

        mockMvc.perform(put("/restaurantes/{id}", restauranteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + restauranteId + "\",\"nome\":\"Restaurante Atualizado\",\"cep\":\"24355220\",\"logradouro\":\"Rua B\",\"bairro\":\"Bairro C\",\"cidade\":\"Cidade D\",\"uf\":\"UF\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restauranteId.toString()))
                .andExpect(jsonPath("$.nome").value("Restaurante Atualizado"))
                .andExpect(jsonPath("$.cep").value("24355220"))
                .andExpect(jsonPath("$.logradouro").value("Rua B"))
                .andExpect(jsonPath("$.bairro").value("Bairro C"))
                .andExpect(jsonPath("$.cidade").value("Cidade D"))
                .andExpect(jsonPath("$.uf").value("UF"));
    }

    @Test
    public void testCreateRestaurante_Failure() throws Exception {
        when(restauranteService.createRestaurante(any(RestauranteDTO.class))).thenThrow(new RuntimeException());

        mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Restaurante 1\",\"cep\":\"24355210\",\"logradouro\":\"Rua A\",\"bairro\":\"Bairro B\",\"cidade\":\"Cidade C\",\"uf\":\"UF\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteRestauranteById_Failure() throws Exception {
        UUID restauranteId = UUID.randomUUID();
        doThrow(new RuntimeException()).when(restauranteService).deleteRestauranteById(restauranteId);

        mockMvc.perform(delete("/restaurantes/" + restauranteId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateRestaurante_Failure() throws Exception {
        UUID restauranteId = UUID.randomUUID();
        RestauranteDTO restauranteDTO = new RestauranteDTO();
        restauranteDTO.setNome("Restaurante Atualizado");
        restauranteDTO.setCep("24355220");
        restauranteDTO.setLogradouro("Rua B");
        restauranteDTO.setBairro("Bairro C");
        restauranteDTO.setCidade("Cidade D");
        restauranteDTO.setUf("UF");

        when(restauranteService.updateRestaurante(any(UUID.class), any(RestauranteDTO.class))).thenThrow(new RuntimeException());

        mockMvc.perform(put("/restaurantes/{id}", restauranteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Restaurante Atualizado\",\"cep\":\"24355220\",\"logradouro\":\"Rua B\",\"bairro\":\"Bairro C\",\"cidade\":\"Cidade D\",\"uf\":\"UF\"}"))
                .andExpect(status().isNotFound());
    }
}