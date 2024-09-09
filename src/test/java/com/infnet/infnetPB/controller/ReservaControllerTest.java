package com.infnet.infnetPB.controller;

import com.infnet.infnetPB.DTO.ReservaDTO;
import com.infnet.infnetPB.client.NotificationClient;
import com.infnet.infnetPB.event.MesaReservadaEvent;
import com.infnet.infnetPB.model.Mesa;
import com.infnet.infnetPB.model.Reserva;
import com.infnet.infnetPB.model.Restaurante;
import com.infnet.infnetPB.model.history.ReservaHistory;
import com.infnet.infnetPB.repository.MesaRepository;
import com.infnet.infnetPB.repository.ReservaRepository;
import com.infnet.infnetPB.repository.RestauranteRepository;
import com.infnet.infnetPB.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ReservaService reservaService;

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private ReservaController reservaController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(reservaController).build();
    }

    @Test
    public void testCreateReserva() throws Exception {
        ReservaDTO reservaDTO = new ReservaDTO();
        UUID reservaId = UUID.randomUUID();
        UUID mesaId = UUID.randomUUID();
        UUID restauranteId = UUID.randomUUID();

        reservaDTO.setId(reservaId);
        reservaDTO.setMesaId(mesaId);
        reservaDTO.setRestauranteId(restauranteId);
        reservaDTO.setQuantidadePessoas(2);
        reservaDTO.setDataReserva(LocalDate.now());

        Mesa mesa = new Mesa();
        mesa.setId(mesaId);

        Restaurante restaurante = new Restaurante();
        restaurante.setId(restauranteId);

        Reserva reserva = new Reserva();
        reserva.setId(reservaId);
        reserva.setMesa(mesa);
        reserva.setRestaurante(restaurante);
        reserva.setQuantidadePessoas(reservaDTO.getQuantidadePessoas());
        reserva.setDataReserva(reservaDTO.getDataReserva());

        when(mesaRepository.findById(mesaId)).thenReturn(Optional.of(mesa));
        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(MesaReservadaEvent.class));

        NotificationClient.NotificationRequest notificationRequest = new NotificationClient.NotificationRequest();
        notificationRequest.setTo("rafaelloureiro2002@gmail.com");
        notificationRequest.setSubject("Nova Reserva Criada");
        notificationRequest.setBody("Uma nova reserva foi criada.");
        doNothing().when(notificationClient).sendNotification(any(NotificationClient.NotificationRequest.class));

        when(reservaService.createReserva(any(ReservaDTO.class))).thenReturn(reservaDTO);

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + reservaId + "\",\"mesaId\":\"" + mesaId + "\",\"restauranteId\":\"" + restauranteId + "\",\"quantidadePessoas\":\"2\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reservaId.toString()))
                .andExpect(jsonPath("$.quantidadePessoas").value(2));
    }

    @Test
    public void testGetAllReservas() throws Exception {
        ReservaDTO reserva1 = new ReservaDTO();
        UUID reservaId1 = UUID.randomUUID();
        reserva1.setId(reservaId1);
        reserva1.setQuantidadePessoas(2);

        ReservaDTO reserva2 = new ReservaDTO();
        UUID reservaId2 = UUID.randomUUID();
        reserva2.setId(reservaId2);
        reserva2.setQuantidadePessoas(2);

        List<ReservaDTO> reservaList = Arrays.asList(reserva1, reserva2);

        when(reservaService.getAllReservas()).thenReturn(reservaList);

        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reservaId1.toString()))
                .andExpect(jsonPath("$[0].quantidadePessoas").value(2))
                .andExpect(jsonPath("$[1].id").value(reservaId2.toString()))
                .andExpect(jsonPath("$[1].quantidadePessoas").value(2));
    }

    @Test
    public void testGetReservaById() throws Exception {
        UUID reservaId = UUID.randomUUID();

        ReservaDTO reservaDTO = new ReservaDTO();
        reservaDTO.setId(reservaId);
        reservaDTO.setQuantidadePessoas(2);

        when(reservaService.getReservaById(reservaId)).thenReturn(reservaDTO);

        mockMvc.perform(get("/reservas/{id}", reservaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservaId.toString()))
                .andExpect(jsonPath("$.quantidadePessoas").value(2));
    }

    @Test
    public void testDeleteReservaById() throws Exception {
        UUID reservaId = UUID.randomUUID();
        doNothing().when(reservaService).deleteReservaById(reservaId);

        mockMvc.perform(delete("/reservas/" + reservaId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAllReservaHistories() throws Exception {
        ReservaHistory history1 = new ReservaHistory();
        UUID historyId1 = UUID.randomUUID();
        history1.setId(historyId1);

        ReservaHistory history2 = new ReservaHistory();
        UUID historyId2 = UUID.randomUUID();
        history2.setId(historyId2);

        List<ReservaHistory> historyList = Arrays.asList(history1, history2);

        when(reservaService.getAllReservaHistories()).thenReturn(historyList);

        mockMvc.perform(get("/reservas/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(historyId1.toString()))
                .andExpect(jsonPath("$[1].id").value(historyId2.toString()));
    }

    @Test
    public void testUpdateReserva() throws Exception {
        UUID reservaId = UUID.randomUUID();
        UUID mesaId = UUID.randomUUID();
        UUID restauranteId = UUID.randomUUID();

        ReservaDTO reservaDTO = new ReservaDTO();
        reservaDTO.setId(reservaId);
        reservaDTO.setMesaId(mesaId);
        reservaDTO.setRestauranteId(restauranteId);
        reservaDTO.setQuantidadePessoas(4);
        reservaDTO.setDataReserva(LocalDate.now());

        Mesa mesa = new Mesa();
        mesa.setId(mesaId);

        Restaurante restaurante = new Restaurante();
        restaurante.setId(restauranteId);

        Reserva reserva = new Reserva();
        reserva.setId(reservaId);
        reserva.setMesa(mesa);
        reserva.setRestaurante(restaurante);
        reserva.setQuantidadePessoas(reservaDTO.getQuantidadePessoas());
        reserva.setDataReserva(reservaDTO.getDataReserva());

        when(mesaRepository.findById(mesaId)).thenReturn(Optional.of(mesa));
        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante));
        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        when(reservaService.updateReserva(any(UUID.class), any(ReservaDTO.class))).thenReturn(reservaDTO);

        mockMvc.perform(put("/reservas/{id}", reservaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + reservaId + "\",\"mesaId\":\"" + mesaId + "\",\"restauranteId\":\"" + restauranteId + "\",\"quantidadePessoas\":\"4\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservaId.toString()))
                .andExpect(jsonPath("$.quantidadePessoas").value(4));
    }

    @Test
    public void testCreateReservaFailure() throws Exception {
        ReservaDTO reservaDTO = new ReservaDTO();
        UUID reservaId = UUID.randomUUID();
        UUID mesaId = UUID.randomUUID();
        UUID restauranteId = UUID.randomUUID();

        reservaDTO.setId(reservaId);
        reservaDTO.setMesaId(mesaId);
        reservaDTO.setRestauranteId(restauranteId);
        reservaDTO.setQuantidadePessoas(2);
        reservaDTO.setDataReserva(LocalDate.now());

        when(mesaRepository.findById(mesaId)).thenReturn(Optional.of(new Mesa()));
        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(new Restaurante()));
        when(reservaService.createReserva(any(ReservaDTO.class))).thenThrow(new RuntimeException("Erro ao criar reserva"));

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + reservaId + "\",\"mesaId\":\"" + mesaId + "\",\"restauranteId\":\"" + restauranteId + "\",\"quantidadePessoas\":\"2\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteReservaByIdFailure() throws Exception {
        UUID reservaId = UUID.randomUUID();
        doThrow(new RuntimeException("Erro ao deletar reserva")).when(reservaService).deleteReservaById(reservaId);

        mockMvc.perform(delete("/reservas/" + reservaId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateReservaFailure() throws Exception {
        UUID reservaId = UUID.randomUUID();
        UUID mesaId = UUID.randomUUID();
        UUID restauranteId = UUID.randomUUID();

        ReservaDTO reservaDTO = new ReservaDTO();
        reservaDTO.setId(reservaId);
        reservaDTO.setMesaId(mesaId);
        reservaDTO.setRestauranteId(restauranteId);
        reservaDTO.setQuantidadePessoas(4);
        reservaDTO.setDataReserva(LocalDate.now());

        when(mesaRepository.findById(mesaId)).thenReturn(Optional.of(new Mesa()));
        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(new Restaurante()));
        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(new Reserva()));
        when(reservaService.updateReserva(any(UUID.class), any(ReservaDTO.class))).thenThrow(new RuntimeException("Erro ao atualizar reserva"));

        mockMvc.perform(put("/reservas/{id}", reservaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + reservaId + "\",\"mesaId\":\"" + mesaId + "\",\"restauranteId\":\"" + restauranteId + "\",\"quantidadePessoas\":\"4\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetReservaByIdNotFound() throws Exception {
        UUID reservaId = UUID.randomUUID();

        when(reservaService.getReservaById(reservaId)).thenThrow(new RuntimeException("Reserva não encontrada"));

        mockMvc.perform(get("/reservas/{id}", reservaId))
                .andExpect(status().isNotFound());
    }


}