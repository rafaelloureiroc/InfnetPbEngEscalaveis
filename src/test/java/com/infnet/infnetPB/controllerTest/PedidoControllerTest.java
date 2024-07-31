package com.infnet.infnetPB.controllerTest;

import com.infnet.infnetPB.DTO.PedidoDTO;
import com.infnet.infnetPB.controller.PedidoController;
import com.infnet.infnetPB.model.history.PedidoHistory;
import com.infnet.infnetPB.service.PedidoService;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(pedidoController).build();
    }
    @Test
    public void testCreatePedido() throws Exception {
        PedidoDTO pedidoDTO = new PedidoDTO();
        UUID pedidoId = UUID.randomUUID();
        pedidoDTO.setId(pedidoId);
        pedidoDTO.setDescricaoPedido("Pedido 1");

        when(pedidoService.createPedido(any(PedidoDTO.class))).thenReturn(pedidoDTO);

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + pedidoId + "\",\"descricaoPedido\":\"Pedido 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(pedidoId.toString()))
                .andExpect(jsonPath("$.descricaoPedido").value("Pedido 1"));
    }

    @Test
    public void testGetAllPedidos() throws Exception {
        PedidoDTO pedido1 = new PedidoDTO();
        UUID pedidoId1 = UUID.randomUUID();
        pedido1.setId(pedidoId1);
        pedido1.setDescricaoPedido("Pedido 1");

        PedidoDTO pedido2 = new PedidoDTO();
        UUID pedidoId2 = UUID.randomUUID();
        pedido2.setId(pedidoId2);
        pedido2.setDescricaoPedido("Pedido 2");

        List<PedidoDTO> pedidoList = Arrays.asList(pedido1, pedido2);

        when(pedidoService.getAllPedidos()).thenReturn(pedidoList);

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(pedidoId1.toString()))
                .andExpect(jsonPath("$[0].descricaoPedido").value("Pedido 1"))
                .andExpect(jsonPath("$[1].id").value(pedidoId2.toString()))
                .andExpect(jsonPath("$[1].descricaoPedido").value("Pedido 2"));
    }

    @Test
    public void testDeletePedidoById() throws Exception {
        UUID pedidoId = UUID.randomUUID();
        doNothing().when(pedidoService).deletePedidoById(pedidoId);

        mockMvc.perform(delete("/pedidos/" + pedidoId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAllPedidoHistory() throws Exception {
        PedidoHistory history1 = new PedidoHistory();
        UUID historyId1 = UUID.randomUUID();
        history1.setId(historyId1);

        PedidoHistory history2 = new PedidoHistory();
        UUID historyId2 = UUID.randomUUID();
        history2.setId(historyId2);

        List<PedidoHistory> historyList = Arrays.asList(history1, history2);

        when(pedidoService.getAllPedidoHistories()).thenReturn(historyList);

        mockMvc.perform(get("/pedidos/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(historyId1.toString()))
                .andExpect(jsonPath("$[1].id").value(historyId2.toString()));
    }
}