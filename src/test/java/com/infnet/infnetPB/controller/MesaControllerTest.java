package com.infnet.infnetPB.controller;

import com.infnet.infnetPB.DTO.MesaDTO;
import com.infnet.infnetPB.model.history.MesaHistory;
import com.infnet.infnetPB.service.MesaService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MesaService mesaService;

    @InjectMocks
    private MesaController mesaController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(mesaController).build();
    }

    @Test
    public void testCreateMesa() throws Exception {
        MesaDTO mesaDTO = new MesaDTO();
        UUID mesaId = UUID.randomUUID();
        mesaDTO.setId(mesaId);
        mesaDTO.setInfoAdicional("Mesa no canto do bar");

        when(mesaService.createMesa(any(MesaDTO.class))).thenReturn(mesaDTO);

        mockMvc.perform(post("/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + mesaId + "\",\"infoAdicional\":\"Mesa no canto do bar\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mesaId.toString()))
                .andExpect(jsonPath("$.infoAdicional").value("Mesa no canto do bar"));
    }

    @Test
    public void testCreateMesaFailure() throws Exception {
        when(mesaService.createMesa(any(MesaDTO.class))).thenThrow(new RuntimeException());

        mockMvc.perform(post("/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"infoAdicional\":\"Mesa no canto do bar\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateMesa() throws Exception {
        UUID mesaId = UUID.randomUUID();
        MesaDTO mesaDTO = new MesaDTO();
        mesaDTO.setId(mesaId);
        mesaDTO.setInfoAdicional("Mesa reformada");

        when(mesaService.updateMesa(any(UUID.class), any(MesaDTO.class))).thenReturn(mesaDTO);

        mockMvc.perform(put("/mesas/{id}", mesaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + mesaId + "\",\"infoAdicional\":\"Mesa reformada\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mesaId.toString()))
                .andExpect(jsonPath("$.infoAdicional").value("Mesa reformada"));
    }

    @Test
    public void testUpdateMesaFailure() throws Exception {
        UUID mesaId = UUID.randomUUID();
        when(mesaService.updateMesa(any(UUID.class), any(MesaDTO.class))).thenThrow(new RuntimeException());

        mockMvc.perform(put("/mesas/{id}", mesaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"infoAdicional\":\"Mesa reformada\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteMesa() throws Exception {
        UUID mesaId = UUID.randomUUID();

        mockMvc.perform(delete("/mesas/{id}", mesaId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteMesaFailure() throws Exception {
        UUID mesaId = UUID.randomUUID();
        doThrow(new RuntimeException()).when(mesaService).deleteMesaById(mesaId);

        mockMvc.perform(delete("/mesas/{id}", mesaId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetMesaById() throws Exception {
        UUID mesaId = UUID.randomUUID();
        MesaDTO mesaDTO = new MesaDTO();
        mesaDTO.setId(mesaId);
        mesaDTO.setInfoAdicional("Mesa no canto do bar");

        when(mesaService.getMesaById(mesaId)).thenReturn(mesaDTO);

        mockMvc.perform(get("/mesas/{id}", mesaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mesaId.toString()))
                .andExpect(jsonPath("$.infoAdicional").value("Mesa no canto do bar"));
    }

    @Test
    public void testGetMesaByIdFailure() throws Exception {
        UUID mesaId = UUID.randomUUID();
        when(mesaService.getMesaById(mesaId)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/mesas/{id}", mesaId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllMesas() throws Exception {
        MesaDTO mesa1 = new MesaDTO();
        UUID mesaId1 = UUID.randomUUID();
        mesa1.setId(mesaId1);
        mesa1.setInfoAdicional("Mesa no canto do bar");

        MesaDTO mesa2 = new MesaDTO();
        UUID mesaId2 = UUID.randomUUID();
        mesa2.setId(mesaId2);
        mesa2.setInfoAdicional("Mesa no canto do bar 2");

        List<MesaDTO> mesaList = Arrays.asList(mesa1, mesa2);

        when(mesaService.getAllMesas()).thenReturn(mesaList);

        mockMvc.perform(get("/mesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mesaId1.toString()))
                .andExpect(jsonPath("$[0].infoAdicional").value("Mesa no canto do bar"))
                .andExpect(jsonPath("$[1].id").value(mesaId2.toString()))
                .andExpect(jsonPath("$[1].infoAdicional").value("Mesa no canto do bar 2"));
    }

    @Test
    public void testGetAllMesaHistories() throws Exception {
        MesaHistory history1 = new MesaHistory();
        UUID mesaHistoryId1 = UUID.randomUUID();
        history1.setId(mesaHistoryId1);

        MesaHistory history2 = new MesaHistory();
        UUID mesaHistoryId2 = UUID.randomUUID();
        history2.setId(mesaHistoryId2);

        List<MesaHistory> historyList = Arrays.asList(history1, history2);

        when(mesaService.getAllMesaHistories()).thenReturn(historyList);

        mockMvc.perform(get("/mesas/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mesaHistoryId1.toString()))
                .andExpect(jsonPath("$[1].id").value(mesaHistoryId2.toString()));
    }

}