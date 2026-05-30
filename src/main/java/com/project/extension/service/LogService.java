package com.project.extension.service;

import com.project.extension.entity.Categoria;
import com.project.extension.entity.Log;
import com.project.extension.repository.CategoriaRepository;
import com.project.extension.repository.LogRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LogService {
    private final LogRepository logRepository;
    private final CategoriaRepository categoriaRepository;

    @Lazy
    @Autowired
    private LogService self;

    private final Map<String, Categoria> categoriaCache = new HashMap<>();

    public LogService(LogRepository logRepository, CategoriaRepository categoriaRepository) {
        this.logRepository = logRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @PostConstruct
    public void inicializar() {
        categoriaRepository.findAll().forEach(categoria -> {
            categoriaCache.put(categoria.getNome().toUpperCase(), categoria);
        });
    }

    @Async
    public void salvarLogAsync(String nivel, String mensagem, Throwable throwable) {
        Categoria categoria = categoriaCache.get(nivel.toUpperCase());

        if (categoria == null) {
            log.warn("Categoria de log '{}' não encontrada no banco. Usando INFO como fallback.", nivel);
            categoria = categoriaCache.get("INFO");
        }

        if (categoria == null) {
            log.error("Categoria INFO ausente — log de negócio não será persistido: {}", mensagem);
            return;
        }

        String mensagemCompleta = throwable != null
                ? mensagem + "\nStack Trace: " + formatarStackTrace(throwable)
                : mensagem;

        Log novoLog = new Log(LocalDateTime.now(), mensagemCompleta, categoria);
        try {
            logRepository.save(novoLog);
        } catch (Exception e) {
            log.error("Falha ao persistir log de negócio no banco: {}", e.getMessage());
        }
    }

    private String formatarStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.toString()).append("\n");
        for (StackTraceElement element : t.getStackTrace()) {
            sb.append("\t").append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    public void info(String mensagem) {
        self.salvarLogAsync("INFO", mensagem, null);
    }

    public void success(String mensagem) {
        self.salvarLogAsync("SUCCESS", mensagem, null);
    }

    public void error(String mensagem, Throwable e) {
        self.salvarLogAsync("ERROR", mensagem, e);
    }

    public void error(String mensagem) {
        self.salvarLogAsync("ERROR", mensagem, null);
    }

    public void debug(String mensagem) {
        self.salvarLogAsync("DEBUG", mensagem, null);
    }

    public void warning(String mensagem) {
        self.salvarLogAsync("WARNING", mensagem, null);
    }

    public void fatal(String mensagem, Throwable e) {
        self.salvarLogAsync("FATAL", mensagem, e);
    }
}
