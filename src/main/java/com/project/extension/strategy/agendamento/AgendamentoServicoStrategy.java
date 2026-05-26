package com.project.extension.strategy.agendamento;

import com.project.extension.entity.*;
import com.project.extension.exception.RegraNegocioException;
import com.project.extension.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@Component("SERVICO")
@AllArgsConstructor
@Slf4j
public class AgendamentoServicoStrategy implements AgendamentoStrategy {

    private final EnderecoService enderecoService;
    private final FuncionarioService funcionarioService;
    private final EtapaService etapaService;
    private final StatusService statusService;
    private final ServicoService servicoService;

    private String normalizarTexto(String valor) {
        if (valor == null) {
            return "";
        }

        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('_', ' ')
                .trim()
                .toUpperCase();
    }

    @Override
    public Agendamento agendar(Agendamento agendamento) {
        Servico servico = agendamento.getServico();
        Servico servicoSalvo = servicoService.buscarPorId(servico.getId());
        String etapaAtual = servicoSalvo != null && servicoSalvo.getEtapa() != null
                ? servicoSalvo.getEtapa().getNome()
                : null;

        if (servicoSalvo == null) {
            throw new RegraNegocioException("Serviço não encontrado para criar o agendamento.");
        }

        String etapaNorm = normalizarTexto(etapaAtual);
        if (!"ORCAMENTO APROVADO".equals(etapaNorm) && !"AGUARDANDO AGENDA DE SERVICO/INSTALACAO".equals(etapaNorm)) {
            throw new RegraNegocioException(
                    String.format(
                            "Só é possível agendar serviço quando a etapa estiver como ORÇAMENTO APROVADO ou AGUARDANDO AGENDA DE SERVIÇO/INSTALAÇÃO. Etapa atual: %s.",
                            etapaAtual == null || etapaAtual.isBlank() ? "não definida" : etapaAtual
                    )
            );
        }
        agendamento.setTipoAgendamento(TipoAgendamento.SERVICO);

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

                funcionariosSalvos.add(funcionarioSalvo);
            }

            agendamento.setFuncionarios(funcionariosSalvos);
        }

        // Mantém os produtos passados para fins de rastreamento (sem reserva de estoque)
        if (agendamento.getAgendamentoProdutos() == null) {
            agendamento.setAgendamentoProdutos(new ArrayList<>());
        } else {
            for (AgendamentoProduto ap : agendamento.getAgendamentoProdutos()) {
                ap.setAgendamento(agendamento);
            }
        }

        if (agendamento.getEndereco() != null) {
            var enderecoSalvo = enderecoService.buscarPorCep(agendamento.getEndereco().getCep());
            if (enderecoSalvo == null) {
                enderecoSalvo = enderecoService.cadastrar(agendamento.getEndereco());
            }
            agendamento.setEndereco(enderecoSalvo);
        }

        if (agendamento.getStatusAgendamento() != null) {
            String tipo = agendamento.getStatusAgendamento().getTipo();
            String nome = agendamento.getStatusAgendamento().getNome();

            Status statusAgendamento = statusService.buscarPorTipoAndStatus(tipo, nome);
            if (statusAgendamento == null) {
                statusAgendamento = statusService.cadastrar(new Status(tipo, nome));
            }

            agendamento.setStatusAgendamento(statusAgendamento);
        }

        Etapa etapaPedido = etapaService.buscarPorTipoAndEtapa("PEDIDO", "SERVIÇO AGENDADO");
        if (etapaPedido == null) {
            etapaPedido = etapaService.cadastrar(new Etapa("PEDIDO", "SERVIÇO AGENDADO"));
        }

        // Ao vincular agendamento de serviço, serviço e pedido devem ficar ativos.
        servicoSalvo.setAtivo(true);
        if (servicoSalvo.getPedido() != null) {
            servicoSalvo.getPedido().setAtivo(true);
        }

        servicoSalvo.setEtapa(etapaPedido);
        servicoService.editar(servicoSalvo, servicoSalvo.getId());

        return agendamento;
    }
}
