package com.infnet.infnetPB.service;

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
import com.infnet.infnetPB.repository.historyRepository.ReservaHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@Transactional
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ReservaHistoryRepository reservaHistoryRepository;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final Logger logger = LoggerFactory.getLogger(MesaService.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    public ReservaDTO createReserva(ReservaDTO reservaDTO) {
        Optional<Mesa> mesaOptional = mesaRepository.findById(reservaDTO.getMesaId());
        Mesa mesa = mesaOptional.orElseThrow(() -> new RuntimeException("Mesa não encontrada"));

        if (mesa.getReserva() != null) {
            logger.error("A Mesa já está reservada");
        }

        Optional<Restaurante> restauranteOptional = restauranteRepository.findById(reservaDTO.getRestauranteId());
        Restaurante restaurante = restauranteOptional.orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        Reserva reserva = new Reserva();
        reserva.setDataReserva(reservaDTO.getDataReserva());
        reserva.setQuantidadePessoas(reservaDTO.getQuantidadePessoas());
        reserva.setMesa(mesa);
        reserva.setRestaurante(restaurante);

        Reserva savedReserva = reservaRepository.save(reserva);
        saveReservaHistory(savedReserva, "CREATE");

        mesa.setReserva(savedReserva);
        mesaRepository.save(mesa);

        MesaReservadaEvent event = new MesaReservadaEvent(
                mesa.getId(),
                restaurante.getId(),
                reserva.getDataReserva());

        logger.info("Tentando enviar evento mesaReservada: {}", event);

        boolean success = sendEventWithRetry(event, "mesaExchange", "mesaReservada");

        if (success) {
            logger.info("Evento mesaReservada enviado com sucesso.");
        } else {
            logger.error("Falha ao enviar evento mesaReservada após {} tentativas.", MAX_RETRIES);
        }

        NotificationClient.NotificationRequest notificationRequest = new NotificationClient.NotificationRequest();
        notificationRequest.setTo("rafaelloureiro2002@gmail.com");
        notificationRequest.setSubject("Nova Reserva Criada");
        notificationRequest.setBody("Uma nova reserva foi criada.");

        try {
            notificationClient.sendNotification(notificationRequest);
        } catch (Exception e) {
            logger.error("Falha ao comunicar com serviço de notificação: " + e.getMessage());
        }

        return mapToDTO(savedReserva);
    }

    private boolean sendEventWithRetry(Object event, String exchange, String routingKey) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                rabbitTemplate.convertAndSend(exchange, routingKey, event);
                return true;
            } catch (Exception e) {
                logger.error("Erro ao enviar evento (tentativa {}): {}", attempt, e.getMessage());
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

    public List<ReservaDTO> getAllReservas() {
        List<Reserva> reservas = reservaRepository.findAll();
        return reservas.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ReservaDTO getReservaById(UUID id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        return mapToDTO(reserva);
    }

    public void deleteReservaById(UUID id) {
        reservaRepository.deleteById(id);
    }

    public List<ReservaHistory> getAllReservaHistories() {
        return reservaHistoryRepository.findAll();
    }

    private void saveReservaHistory(Reserva reserva, String operation) {
        ReservaHistory history = new ReservaHistory();
        history.setReserva(reserva);
        history.setDataReserva(reserva.getDataReserva().atStartOfDay());
        history.setQuantidadePessoas(reserva.getQuantidadePessoas());
        history.setTimestamp(LocalDateTime.now());
        history.setOperation(operation);
        reservaHistoryRepository.save(history);
    }

    private ReservaDTO mapToDTO(Reserva reserva) {
        ReservaDTO reservaDTO = new ReservaDTO();
        reservaDTO.setId(reserva.getId());
        reservaDTO.setDataReserva(reserva.getDataReserva());
        reservaDTO.setQuantidadePessoas(reserva.getQuantidadePessoas());
        reservaDTO.setRestauranteId(reserva.getRestaurante().getId());
        reservaDTO.setMesaId(reserva.getMesa().getId());
        return reservaDTO;
    }
}