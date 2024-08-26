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

    public PedidoDTO createPedido(PedidoDTO pedidoDTO) {
        Optional<Restaurante> restauranteOptional = restauranteRepository.findById(pedidoDTO.getRestauranteId());
        Restaurante restaurante = restauranteOptional.orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        Optional<Mesa> mesaOptional = mesaRepository.findById(pedidoDTO.getMesaId());
        Mesa mesa = mesaOptional.orElseThrow(() -> new RuntimeException("Mesa não encontrada"));

        if (mesa.getPedido() != null) {
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
        try {
            rabbitTemplate.convertAndSend("pedidoExchange", "pedidoCriado", event);
        } catch (Exception e) {
            System.err.println("(Pedido) Falha ao comunicar com RabbitMQ: " + e.getMessage());
        }

        return mapToDTO(savedPedido);
    }

    public List<PedidoDTO> getAllPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deletePedidoById(UUID id) {
        pedidoRepository.deleteById(id);
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