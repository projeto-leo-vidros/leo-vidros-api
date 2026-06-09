package com.project.extension.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SecurityService {
    
    public void validateResourceAccess(Integer resourceId, String operation) {
        if (resourceId == null || resourceId <= 0) {
            log.warn("Tentativa de acesso com ID inválido: {} - Operação: {}", resourceId, operation);
            throw new SecurityException("ID de recurso inválido");
        }
        
    }
    
    public boolean isValidId(Integer id) {
        return id != null && id > 0;
    }
}