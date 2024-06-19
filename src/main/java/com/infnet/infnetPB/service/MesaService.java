package com.infnet.infnetPB.service;

import com.infnet.infnetPB.DTO.MesaDTO;
import com.infnet.infnetPB.DTO.PedidoDTO;
import com.infnet.infnetPB.model.history.MesaHistory;
import com.infnet.infnetPB.model.Pedido;
import com.infnet.infnetPB.model.Restaurante;
import com.infnet.infnetPB.repository.historyRepository.MesaHistoryRepository;
import com.infnet.infnetPB.repository.RestauranteRepository;
import jakarta.transaction.Transactional;
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

    public MesaDTO createMesa(MesaDTO mesaDTO) {
        Optional<Restaurante> restauranteOptional = restauranteRepository.findById(mesaDTO.getRestauranteId());
        Restaurante restaurante = restauranteOptional.orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        Mesa mesa = new Mesa();
        mesa.setQtdAssentosMax(mesaDTO.getQtdAssentosMax());
        mesa.setInfoAdicional(mesaDTO.getInfoAdicional());
        mesa.setRestaurante(restaurante);

        Mesa savedMesa = mesaRepository.save(mesa);
        saveMesaHistory(savedMesa, "CREATE");

        return mapToDTO(savedMesa);
    }
/*
    public MesaDTO updateMesa(UUID id, MesaDTO mesaDTO) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada"));

        mesa.setQtdAssentosMax(mesaDTO.getQtdAssentosMax());
        mesa.setInfoAdicional(mesaDTO.getInfoAdicional());

        Mesa updatedMesa = mesaRepository.save(mesa);
        saveMesaHistory(updatedMesa, "UPDATE");

        return mapToDTO(updatedMesa);
    }
*/
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