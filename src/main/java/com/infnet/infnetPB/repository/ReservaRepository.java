package com.infnet.infnetPB.repository;

import com.infnet.infnetPB.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
}
