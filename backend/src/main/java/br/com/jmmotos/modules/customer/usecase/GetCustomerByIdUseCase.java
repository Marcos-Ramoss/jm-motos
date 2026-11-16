package br.com.jmmotos.modules.customer.usecase;

import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.customer.dto.ClienteResponse;
import br.com.jmmotos.modules.customer.model.Cliente;
import br.com.jmmotos.modules.customer.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCustomerByIdUseCase {

    private final ClienteRepository clienteRepository;

    public GetCustomerByIdUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public ClienteResponse execute(Long id) {
        Cliente c = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

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

