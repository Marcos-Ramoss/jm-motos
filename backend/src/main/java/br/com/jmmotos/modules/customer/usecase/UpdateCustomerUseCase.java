package br.com.jmmotos.modules.customer.usecase;

import br.com.jmmotos.exception.ConflictException;
import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.customer.dto.ClienteRequest;
import br.com.jmmotos.modules.customer.dto.ClienteResponse;
import br.com.jmmotos.modules.customer.model.Cliente;
import br.com.jmmotos.modules.customer.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCustomerUseCase {

    private final ClienteRepository clienteRepository;

    public UpdateCustomerUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponse execute(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        String cpfLimpo = request.getCpf().replaceAll("\\D", "");

        if (!cliente.getCpf().equals(cpfLimpo) && clienteRepository.existsByCpf(cpfLimpo)) {
            throw new ConflictException("Já existe outro cliente cadastrado com o CPF informado");
        }

        cliente.setNome(request.getNome().trim());
        cliente.setCpf(cpfLimpo);
        cliente.setRg(request.getRg() != null ? request.getRg().trim() : null);
        cliente.setCnhNumero(request.getCnhNumero().trim());
        cliente.setCnhCategoria(request.getCnhCategoria() != null ? request.getCnhCategoria().trim().toUpperCase() : "A");
        cliente.setCnhValidade(request.getCnhValidade());
        cliente.setTelefone(request.getTelefone().trim());
        cliente.setEndereco(request.getEndereco() != null ? request.getEndereco().trim() : null);
        cliente.setMunicipio(request.getMunicipio() != null ? request.getMunicipio().trim() : "Manaus");

        Cliente atualizado = clienteRepository.save(cliente);
        return mapToResponse(atualizado);
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

