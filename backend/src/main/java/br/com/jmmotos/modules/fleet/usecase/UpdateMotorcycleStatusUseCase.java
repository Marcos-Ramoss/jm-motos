package br.com.jmmotos.modules.fleet.usecase;

import br.com.jmmotos.exception.BusinessRuleException;
import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.fleet.dto.MotocicletaResponse;
import br.com.jmmotos.modules.fleet.dto.MotocicletaStatusUpdateRequest;
import br.com.jmmotos.modules.fleet.model.Motocicleta;
import br.com.jmmotos.modules.fleet.model.StatusOperacional;
import br.com.jmmotos.modules.fleet.repository.MotocicletaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateMotorcycleStatusUseCase {

    private final MotocicletaRepository motocicletaRepository;

    public UpdateMotorcycleStatusUseCase(MotocicletaRepository motocicletaRepository) {
        this.motocicletaRepository = motocicletaRepository;
    }

    @Transactional
    public MotocicletaResponse execute(Long id, MotocicletaStatusUpdateRequest request) {
        Motocicleta moto = motocicletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motocicleta não encontrada com ID: " + id));

        StatusOperacional novoStatus;
        try {
            novoStatus = StatusOperacional.valueOf(request.getStatusOperacional().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Status operacional inválido: " + request.getStatusOperacional());
        }

        moto.setStatusOperacional(novoStatus);
        Motocicleta atualizada = motocicletaRepository.save(moto);

        return MotocicletaResponse.builder()
                .id(atualizada.getId())
                .placa(atualizada.getPlaca())
                .chassi(atualizada.getChassi())
                .marca(atualizada.getMarca())
                .modelo(atualizada.getModelo())
                .ano(atualizada.getAno())
                .renavam(atualizada.getRenavam())
                .odometro(atualizada.getOdometro())
                .dataAquisicao(atualizada.getDataAquisicao())
                .custoAquisicao(atualizada.getCustoAquisicao())
                .municipioOperacao(atualizada.getMunicipioOperacao())
                .statusOperacional(atualizada.getStatusOperacional().name())
                .statusRastreador(atualizada.getStatusRastreador().name())
                .criadoEm(atualizada.getCriadoEm())
                .atualizadoEm(atualizada.getAtualizadoEm())
                .build();
    }
}

