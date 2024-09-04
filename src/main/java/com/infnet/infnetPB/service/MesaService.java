package com.infnet.infnetPB.service;

import com.infnet.infnetPB.DTO.MesaDTO;
import com.infnet.infnetPB.DTO.PedidoDTO;
import com.infnet.infnetPB.event.MesaCadastradaEvent;
import com.infnet.infnetPB.model.history.MesaHistory;
import com.infnet.infnetPB.model.Pedido;
import com.infnet.infnetPB.model.Restaurante;
import com.infnet.infnetPB.repository.historyRepository.MesaHistoryRepository;
import com.infnet.infnetPB.repository.RestauranteRepository;
import jakarta.transaction.Transactional;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.infnet.infnetPB.repository.MesaRepository;
import com.infnet.infnetPB.model.Mesa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private MesaHistoryRepository mesaHistoryRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final static Logger logger = Logger.getLogger(MesaService.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Transactional
    public MesaDTO createMesa(MesaDTO mesaDTO) {
        Optional<Restaurante> restauranteOptional = restauranteRepository.findById(mesaDTO.getRestauranteId());
        Restaurante restaurante = restauranteOptional.orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        Mesa mesa = new Mesa();
        mesa.setQtdAssentosMax(mesaDTO.getQtdAssentosMax());
        mesa.setInfoAdicional(mesaDTO.getInfoAdicional());
        mesa.setRestaurante(restaurante);

        Mesa savedMesa = mesaRepository.save(mesa);
        saveMesaHistory(savedMesa, "CREATE");

        MesaCadastradaEvent event = new MesaCadastradaEvent(
                savedMesa.getId(),
                restaurante.getId(),
                savedMesa.getQtdAssentosMax(),
                savedMesa.getInfoAdicional(),
                "CREATED"
        );

        logger.info("Tentando enviar evento MesaCadastrada: " + event);

        boolean success = sendEventWithRetry(event, "mesaExchange", "mesaCadastrada");

        if (success) {
            logger.info("Evento MesaCadastrada enviado com sucesso.");
            return mapToDTO(savedMesa);
        } else {
            logger.error("Falha ao enviar evento MesaCadastrada após " + MAX_RETRIES + " tentativas.");
            mesaRepository.delete(savedMesa);
            throw new RuntimeException("Falha ao enviar evento MesaCadastrada. Mesa não foi criada.");
        }
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


    public List<MesaDTO> getAllMesas() {
        List<Mesa> mesas = mesaRepository.findAll();
        return mesas.stream()
                .map(this::mapToDTOWithPedidos)
                .collect(Collectors.toList());
    }

    public MesaDTO getMesaById(UUID id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada"));
        return mapToDTOWithPedidos(mesa);
    }

    public void deleteMesaById(UUID id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada"));
        mesaRepository.deleteById(id);
        saveMesaHistory(mesa, "DELETE");
    }

    public List<MesaHistory> getAllMesaHistories() {
        return mesaHistoryRepository.findAll();
    }

    private void saveMesaHistory(Mesa mesa, String operation) {
        MesaHistory mesaHistory = new MesaHistory();
        mesaHistory.setMesa(mesa);
        mesaHistory.setQtdAssentosMax(mesa.getQtdAssentosMax());
        mesaHistory.setInfoAdicional(mesa.getInfoAdicional());
        mesaHistory.setStatus(mesa.getReserva() != null ? "Reservada" : "Disponível");
        mesaHistory.setTimestamp(LocalDateTime.now());
        mesaHistory.setOperation(operation);
        mesaHistoryRepository.save(mesaHistory);
    }

    private MesaDTO mapToDTO(Mesa mesa) {
        MesaDTO mesaDTO = new MesaDTO();
        mesaDTO.setId(mesa.getId());
        mesaDTO.setQtdAssentosMax(mesa.getQtdAssentosMax());
        mesaDTO.setInfoAdicional(mesa.getInfoAdicional());
        mesaDTO.setNomeRestaurante(mesa.getRestaurante().getNome());
        mesaDTO.setRestauranteId(mesa.getRestaurante().getId());

        return mesaDTO;
    }

    private MesaDTO mapToDTOWithPedidos(Mesa mesa) {
        MesaDTO mesaDTO = mapToDTO(mesa);

        if (mesa.getReserva() != null) {
            mesaDTO.setStatus("Reservada");
        } else {
            mesaDTO.setStatus("Disponível");
        }

        if (mesa.getPedido() != null) {
            Pedido pedido = mesa.getPedido();

            PedidoDTO pedidoDTO = new PedidoDTO();
            pedidoDTO.setId(pedido.getId());
            pedidoDTO.setDescricaoPedido(pedido.getDescricaoPedido());
            pedidoDTO.setValorTotal(pedido.getValorTotal());

            List<PedidoDTO> pedidos = new ArrayList<>();
            pedidos.add(pedidoDTO);

            List<String> descricoesPedidos = pedidos.stream()
                    .map(p -> p.getDescricaoPedido() + " - Valor total: " + p.getValorTotal())
                    .collect(Collectors.toList());

            mesaDTO.setPedidos(descricoesPedidos);
        } else {
            mesaDTO.setPedidos(List.of());
        }
        return mesaDTO;
    }
}