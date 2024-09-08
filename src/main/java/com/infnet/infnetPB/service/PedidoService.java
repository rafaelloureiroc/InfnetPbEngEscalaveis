package com.infnet.infnetPB.service;

import com.infnet.infnetPB.DTO.PedidoDTO;
import com.infnet.infnetPB.event.PedidoCriadoEvent;
import com.infnet.infnetPB.model.Mesa;
import com.infnet.infnetPB.model.Pedido;
import com.infnet.infnetPB.model.history.PedidoHistory;
import com.infnet.infnetPB.model.Restaurante;
import com.infnet.infnetPB.repository.MesaRepository;
import com.infnet.infnetPB.repository.historyRepository.PedidoHistoryRepository;
import com.infnet.infnetPB.repository.PedidoRepository;
import com.infnet.infnetPB.repository.RestauranteRepository;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private  PedidoRepository pedidoRepository;

    @Autowired
    private  RestauranteRepository restauranteRepository;

    @Autowired
    private  MesaRepository mesaRepository;

    @Autowired
    private PedidoHistoryRepository pedidoHistoryRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final static Logger logger = Logger.getLogger(RestauranteService.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Transactional
    public PedidoDTO createPedido(PedidoDTO pedidoDTO) {
        Optional<Restaurante> restauranteOptional = restauranteRepository.findById(pedidoDTO.getRestauranteId());
        Restaurante restaurante = restauranteOptional.orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        Optional<Mesa> mesaOptional = mesaRepository.findById(pedidoDTO.getMesaId());
        Mesa mesa = mesaOptional.orElseThrow(() -> new RuntimeException("Mesa não encontrada"));

        if (mesa.getPedido() != null) {
            logger.error("A Mesa já tem um pedido criado.");
            throw new RuntimeException("A Mesa já tem um pedido criado.");
        }

        Pedido pedido = new Pedido();
        pedido.setDescricaoPedido(pedidoDTO.getDescricaoPedido());
        pedido.setValorTotal(pedidoDTO.getValorTotal());
        pedido.setRestaurante(restaurante);
        pedido.setMesa(mesa);

        Pedido savedPedido = pedidoRepository.save(pedido);
        savePedidoHistory(savedPedido, "CREATE");

        PedidoCriadoEvent event = new PedidoCriadoEvent(
                savedPedido.getId(),
                savedPedido.getDescricaoPedido(),
                savedPedido.getValorTotal(),
                savedPedido.getMesa().getId(),
                savedPedido.getRestaurante().getId()
        );

        logger.info("Tentando enviar evento PedidoCriado: " + event);

        CompletableFuture.runAsync(() -> {
            boolean success = sendEventWithRetry(event, "pedidoExchange", "pedidoCriado");
            if (success) {
                logger.info("Evento PedidoCriado enviado com sucesso.");
            } else {
                logger.error("Falha ao enviar evento PedidoCriado após " + MAX_RETRIES + " tentativas.");
            }
        });

        return mapToDTO(savedPedido);
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

    public PedidoDTO updatePedido(UUID id, PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        pedido.setDescricaoPedido(pedidoDTO.getDescricaoPedido());
        pedido.setValorTotal(pedidoDTO.getValorTotal());

        Pedido updatedPedido = pedidoRepository.save(pedido);

        savePedidoHistory(updatedPedido, "UPDATE");

        return mapToDTO(updatedPedido);
    }

    public void deletePedidoById(UUID id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        pedidoRepository.deleteById(id);
        savePedidoHistory(pedido, "DELETE");
    }

    public List<PedidoDTO> getAllPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PedidoHistory> getAllPedidoHistories() {
        return pedidoHistoryRepository.findAll();
    }

    private void savePedidoHistory(Pedido pedido, String operation) {
        PedidoHistory pedidoHistory = new PedidoHistory();
        pedidoHistory.setPedido(pedido);
        pedidoHistory.setDescricaoPedido(pedido.getDescricaoPedido());
        pedidoHistory.setValorTotal(pedido.getValorTotal());
        pedidoHistory.setTimestamp(LocalDateTime.now());
        pedidoHistory.setOperation(operation);
        pedidoHistoryRepository.save(pedidoHistory);
    }


    private PedidoDTO mapToDTO(Pedido pedido) {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(pedido.getId());
        pedidoDTO.setDescricaoPedido(pedido.getDescricaoPedido());
        pedidoDTO.setValorTotal(pedido.getValorTotal());
        pedidoDTO.setRestauranteId(pedido.getRestaurante().getId());
        pedidoDTO.setMesaId(pedido.getMesa().getId());
        return pedidoDTO;
    }
}