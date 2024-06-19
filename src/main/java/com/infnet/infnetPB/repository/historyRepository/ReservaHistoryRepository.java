package com.infnet.infnetPB.repository.historyRepository;

import com.infnet.infnetPB.model.history.ReservaHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReservaHistoryRepository extends JpaRepository<ReservaHistory, UUID> {
}
