package com.infnet.infnetPB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.infnet.infnetPB.model.Mesa;

import java.util.UUID;

public interface MesaRepository extends JpaRepository<Mesa, UUID> {
}