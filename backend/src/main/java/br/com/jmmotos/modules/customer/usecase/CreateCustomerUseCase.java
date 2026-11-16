package br.com.jmmotos.modules.customer.usecase;

import br.com.jmmotos.exception.ConflictException;
import br.com.jmmotos.modules.customer.dto.ClienteRequest;
import br.com.jmmotos.modules.customer.dto.ClienteResponse;
import br.com.jmmotos.modules.customer.model.Cliente;
import br.com.jmmotos.modules.customer.model.ClienteStatus;
import br.com.jmmotos.modules.customer.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCustomerUseCase {

    private final ClienteRepository clienteRepository;

    public CreateCustomerUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponse execute(ClienteRequest request) {
        String cpfLimpo = request.getCpf().replaceAll("\\D", "");

        if (clienteRepository.existsByCpf(cpfLimpo)) {
            throw new ConflictException("Já existe um cliente cadastrado com o CPF informado");
        }

        Cliente cliente = Cliente.builder()
                .nome(request.getNome().trim())
                .cpf(cpfLimpo)
                .rg(request.getRg() != null ? request.getRg().trim() : null)
                .cnhNumero(request.getCnhNumero().trim())
                .cnhCategoria(request.getCnhCategoria() != null ? request.getCnhCategoria().trim().toUpperCase() : "A")
                .cnhValidade(request.getCnhValidade())
                .telefone(request.getTelefone().trim())
                .endereco(request.getEndereco() != null ? request.getEndereco().trim() : null)
                .municipio(request.getMunicipio() != null ? request.getMunicipio().trim() : "Manaus")
                .status(ClienteStatus.ATIVO)
                .build();

        Cliente salvo = clienteRepository.save(cliente);
        return mapToResponse(salvo);
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

