package com.infnet.infnetPB.repository.historyRepository;

import com.infnet.infnetPB.model.history.MesaHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MesaHistoryRepository extends JpaRepository<MesaHistory, UUID> {
}