package com.project.extension.strategy.agendamento;

import com.project.extension.entity.*;
import com.project.extension.exception.RegraNegocioException;
import com.project.extension.exception.naoencontrado.EtapaNaoEncontradoException;
import com.project.extension.repository.PedidoRepository;
import com.project.extension.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component("ORCAMENTO")
@Slf4j
@AllArgsConstructor
public class AgendamentoOrcamentoStrategy implements AgendamentoStrategy {

    private final EtapaService etapaService;
    private final StatusService statusService;
    private final ServicoService servicoService;
    private final EnderecoService enderecoService;
    private final FuncionarioService funcionarioService;
    private final PedidoRepository pedidoRepository;

    @Override
    public Agendamento agendar(Agendamento agendamento) {
        if (agendamento.getTipoAgendamento() == null) {
            throw new IllegalArgumentException("Tipo do agendamento é obrigatório");
        }
        if (agendamento.getDataAgendamento() == null) {
            agendamento.setDataAgendamento(LocalDate.now());
        }
        if (agendamento.getObservacao() == null) {
            agendamento.setObservacao("");
        }

        if (agendamento.getServico() != null) {
            Servico servicoSalvo = servicoService.buscarPorId(agendamento.getServico().getId());
            if (servicoSalvo == null) {
                throw new IllegalArgumentException("Pedido não encontrado no banco");
            }

            if (agendamento.getFuncionarios() != null && !agendamento.getFuncionarios().isEmpty()) {
                if (agendamento.getInicioAgendamento() == null || agendamento.getFimAgendamento() == null) {
                    throw new RegraNegocioException("Horário de início e fim são obrigatórios para validar a disponibilidade dos funcionários.");
                }

                List<Funcionario> funcionariosSalvos = new ArrayList<>();

                for (Funcionario f : agendamento.getFuncionarios()) {
                    Funcionario funcionarioSalvo = null;

                    if (f.getId() != null) {
                        funcionarioSalvo = funcionarioService.buscarPorId(f.getId());
                    } else if (f.getTelefone() != null) {
                        funcionarioSalvo = funcionarioService.buscarPorTelefone(f.getTelefone());
                    }

                    if (funcionarioSalvo == null) {
                        funcionarioSalvo = funcionarioService.cadastrar(f);
                    }

                    boolean conflito = funcionarioService.temConflito(
                            funcionarioSalvo.getId(),
                            agendamento.getDataAgendamento(),
                            agendamento.getInicioAgendamento(),
                            agendamento.getFimAgendamento()
                    );
                    if (conflito) {
                        throw new RegraNegocioException(
                                String.format("Funcionário '%s' possui conflito de horário nesta data e horário.",
                                        funcionarioSalvo.getNome()));
                    }

                    log.info("Funcionário: {} alocado para agendamento de: {}", funcionarioSalvo.getNome(), agendamento.getTipoAgendamento());
                    funcionariosSalvos.add(funcionarioSalvo);
                }

                agendamento.setFuncionarios(funcionariosSalvos);
            }

            Etapa etapa;
            try {
                etapa = etapaService.buscarPorTipoAndEtapa("PEDIDO", "ORÇAMENTO AGENDADO");
            } catch (EtapaNaoEncontradoException e) {
                log.warn("Etapa 'ORÇAMENTO AGENDADO' não encontrada — realizando cadastro automático.");
                etapa = etapaService.cadastrar(new Etapa("PEDIDO", "ORÇAMENTO AGENDADO"));
            }

            servicoSalvo.setEtapa(etapa);
            servicoSalvo.setAtivo(true);
            servicoService.editar(servicoSalvo, servicoSalvo.getId());

            Pedido pedido = servicoSalvo.getPedido();
            if (pedido != null) {
                pedido.setAtivo(true);
                pedidoRepository.save(pedido);
            }

            agendamento.setServico(servicoSalvo);
        }

        if (agendamento.getAgendamentoProdutos() != null && !agendamento.getAgendamentoProdutos().isEmpty()) {
            for (AgendamentoProduto ap : agendamento.getAgendamentoProdutos()) {
                ap.setAgendamento(agendamento);
            }
        }

        if (agendamento.getStatusAgendamento() != null) {
            Status statusAg = statusService.buscarPorTipoAndStatus(
                    agendamento.getStatusAgendamento().getTipo(),
                    agendamento.getStatusAgendamento().getNome()
            );

            if (statusAg == null) {
                statusAg = statusService.cadastrar(
                        new Status(
                                agendamento.getStatusAgendamento().getTipo(),
                                agendamento.getStatusAgendamento().getNome()
                        )
                );
            }

            agendamento.setStatusAgendamento(statusAg);
        }

        if (agendamento.getEndereco() != null) {
            Endereco enderecoSalvo = enderecoService.buscarPorCep(agendamento.getEndereco().getCep());
            if (enderecoSalvo == null) {
                enderecoSalvo = enderecoService.cadastrar(agendamento.getEndereco());
            }
            agendamento.setEndereco(enderecoSalvo);
        }
        return agendamento;
    }
}
