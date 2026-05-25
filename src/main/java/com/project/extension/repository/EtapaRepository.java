package com.project.extension.repository;

import com.project.extension.entity.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EtapaRepository extends JpaRepository<Etapa, Integer> {
    Optional<Etapa> findFirstByTipoAndNome(String tipo, String nome);
    List<Etapa> findByTipo(String tipo);
}
