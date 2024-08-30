package com.infnet.infnetPB.controllerTest;

import com.infnet.infnetPB.DTO.ReservaDTO;
import com.infnet.infnetPB.client.NotificationClient;
import com.infnet.infnetPB.controller.ReservaController;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ReservaService reservaService;

    @MockBean
    private MesaRepository mesaRepository;

    @MockBean
    private RestauranteRepository restauranteRepository;

    @MockBean
    private ReservaRepository reservaRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
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
}