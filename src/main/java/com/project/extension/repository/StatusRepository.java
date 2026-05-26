package com.project.extension.repository;

import com.project.extension.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    Optional<Status> findFirstByTipoAndNome(String tipo, String nome);
}
