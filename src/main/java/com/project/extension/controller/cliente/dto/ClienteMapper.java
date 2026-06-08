package com.project.extension.controller.cliente.dto;

import com.project.extension.controller.valueobject.endereco.EnderecoMapper;
import com.project.extension.controller.valueobject.endereco.EnderecoResponseDto;
import com.project.extension.entity.Cliente;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ClienteMapper {

    private final EnderecoMapper enderecoMapper;

    public Cliente toEntity(ClienteRequestDto dto){
        if(dto == null) return null;

        String cpfSanitizado = dto.cpf() != null ? dto.cpf().replaceAll("\\D", "") : null;
        String cpf = cpfSanitizado != null && cpfSanitizado.isBlank() ? null : cpfSanitizado;

        Cliente cliente = new Cliente(
                dto.nome(),
                cpf,
                dto.email(),
                dto.telefone(),
                dto.status()
        );
        if (dto.enderecos() != null) {
            cliente.setEnderecos(
                    dto.enderecos().stream()
                            .map(enderecoMapper::toEntity)
                            .collect(Collectors.toList())
            );
        } else {
            cliente.setEnderecos(new ArrayList<>());
        }

        return cliente;
    }

    public ClienteResponseDto toResponse(Cliente cliente) {
        if (cliente == null) return null;

        List<EnderecoResponseDto> enderecos = cliente.getEnderecos() != null
                ? cliente.getEnderecos().stream().map(enderecoMapper::toResponse).toList()
                : Collections.emptyList();

        return new ClienteResponseDto(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getStatus(),
                enderecos
        );
    }

    public Cliente toEntity(ClienteResponseDto dto) {
        if (dto == null) return null;

        Cliente cliente = new Cliente(
                dto.nome(),
                dto.cpf(),
                dto.email(),
                dto.telefone(),
                dto.status()
        );

        cliente.setId(dto.id());

        if (dto.enderecos() != null) {
            cliente.setEnderecos(dto.enderecos().stream().map(enderecoMapper::toEntity).toList());
        } else {
            cliente.setEnderecos(new ArrayList<>());
        }

        return cliente;
    }

}
