package com.project.extension.service;

import com.project.extension.config.CacheConfig;
import com.project.extension.controller.dashboard.dto.*;
import com.project.extension.repository.AgendamentoRepository;
import com.project.extension.repository.EstoqueRepository;
import com.project.extension.repository.OrcamentoRepository;
import com.project.extension.repository.PedidoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class DashboardService {

    private EstoqueRepository estoqueRepository;
    private AgendamentoRepository agendamentoRepository;
    private PedidoRepository pedidoRepository;
    private OrcamentoRepository orcamentoRepository;

    public int getItensAbaixoMinimo() {
        return estoqueRepository.countItensAbaixoMinimo();
    }

    public int qtdServicosHoje(){
        return agendamentoRepository.countServicosHoje();
    }

    public List<EstoqueCriticoResponseDto> estoqueCritico() {

        List<Object[]> raw = estoqueRepository.estoqueCriticoRaw();

        return raw.stream()
                .map(r -> new EstoqueCriticoResponseDto(
                        (Integer) r[0] ,
                        toBigDecimal(r[1]),
                        toBigDecimal(r[2]),
                        toBigDecimal(r[3]),
                        (String) r[4],
                        (String) r[5],
                        (String) r[6],
                        (String) r[7],
                        toBigDecimal(r[8]),
                        (Integer) r[9],
                        (Integer) r[10]
                )).toList();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) return new BigDecimal(n.toString());
        throw new IllegalArgumentException("Valor não numérico: " + value);
    }

    public int getQtdAgendamentosHoje(){
        return agendamentoRepository.countQtdAgendamentosHoje();
    }

    public int getQtdAgendamentosFuturos(){
        return agendamentoRepository.countQtdAgendamentosFuturos();
    }

    public Double taxaOcupacaoServicos(){
        return agendamentoRepository.taxaOcupacaoServicos();
    }

    public List<ProximosAgendamentosResponseDto> proximosAgendamentos() {
        return agendamentoRepository.proximosAgendamentos();
    }

    @Cacheable(cacheNames = CacheConfig.FATURAMENTO_MES)
    public FaturamentoMesResponseDto getFaturamentoMes() {
        BigDecimal mesAtual = pedidoRepository.sumFaturamentoMesAtual();
        BigDecimal mesAnterior = pedidoRepository.sumFaturamentoMesAnterior();

        if (mesAtual == null) mesAtual = BigDecimal.ZERO;
        if (mesAnterior == null) mesAnterior = BigDecimal.ZERO;

        Double percentual = null;
        if (mesAnterior.compareTo(BigDecimal.ZERO) > 0) {
            percentual = mesAtual.subtract(mesAnterior)
                    .divide(mesAnterior, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new FaturamentoMesResponseDto(mesAtual, percentual);
    }

    @Cacheable(cacheNames = CacheConfig.FATURAMENTO_ANUAL)
    public FaturamentoAnualResponseDto getFaturamentoAnual() {
        int ano = LocalDate.now().getYear();
        List<Object[]> raw = pedidoRepository.sumFaturamentoPorMesAnoAtual();

        Map<Integer, BigDecimal> porMes = new HashMap<>();
        for (Object[] row : raw) {
            int mes = ((Number) row[0]).intValue();
            BigDecimal valor = toBigDecimal(row[1]);
            porMes.put(mes, valor);
        }

        String[] nomesMeses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

        List<FaturamentoMensalItemDto> meses = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            meses.add(new FaturamentoMensalItemDto(i, nomesMeses[i - 1], porMes.getOrDefault(i, BigDecimal.ZERO)));
        }

        return new FaturamentoAnualResponseDto(ano, meses);
    }

    public OrcamentosAbertosResponseDto getOrcamentosAbertos() {
        int quantidade = orcamentoRepository.countOrcamentosAbertos();
        BigDecimal valorTotal = orcamentoRepository.sumValorOrcamentosAbertos();
        if (valorTotal == null) valorTotal = BigDecimal.ZERO;
        return new OrcamentosAbertosResponseDto(quantidade, valorTotal);
    }
}
