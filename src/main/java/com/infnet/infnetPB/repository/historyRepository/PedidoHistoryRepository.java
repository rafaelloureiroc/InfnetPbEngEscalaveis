package com.infnet.infnetPB.repository.historyRepository;

import com.infnet.infnetPB.model.history.PedidoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PedidoHistoryRepository extends JpaRepository<PedidoHistory, UUID> {
}