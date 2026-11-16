package br.com.jmmotos.modules.customer.usecase;

import br.com.jmmotos.modules.customer.dto.ClienteResponse;
import br.com.jmmotos.modules.customer.model.Cliente;
import br.com.jmmotos.modules.customer.model.ClienteStatus;
import br.com.jmmotos.modules.customer.repository.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListCustomersUseCase {

    private final ClienteRepository clienteRepository;

    public ListCustomersUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponse> execute(String busca, String statusStr, Pageable pageable) {
        ClienteStatus status = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                status = ClienteStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        String termoBusca = (busca != null && !busca.isBlank()) ? busca.trim() : null;
        Page<Cliente> page = clienteRepository.findWithFilters(termoBusca, status, pageable);
        return page.map(this::mapToResponse);
    }

    private ClienteResponse mapToResponse(Cliente c) {
        String cpf = c.getCpf();
        String mascarado = cpf;
        if (cpf != null && cpf.length() == 11) {
            mascarado = "***." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-**";
        }

        return ClienteResponse.builder()
                .id(c.getId())
                .nome(c.getNome())
                .cpf(c.getCpf())
                .cpfMascarado(mascarado)
                .rg(c.getRg())
                .cnhNumero(c.getCnhNumero())
                .cnhCategoria(c.getCnhCategoria())
                .cnhValidade(c.getCnhValidade())
                .telefone(c.getTelefone())
                .endereco(c.getEndereco())
                .municipio(c.getMunicipio())
                .status(c.getStatus().name())
                .criadoEm(c.getCriadoEm())
                .atualizadoEm(c.getAtualizadoEm())
                .build();
    }
}

