package com.infnet.infnetPB.repository;

import com.infnet.infnetPB.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestauranteRepository extends JpaRepository<Restaurante, UUID> {
}
