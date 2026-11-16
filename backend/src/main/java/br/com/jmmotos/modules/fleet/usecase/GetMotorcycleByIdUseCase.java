package br.com.jmmotos.modules.fleet.usecase;

import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.fleet.dto.MotocicletaResponse;
import br.com.jmmotos.modules.fleet.model.Motocicleta;
import br.com.jmmotos.modules.fleet.repository.MotocicletaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetMotorcycleByIdUseCase {

    private final MotocicletaRepository motocicletaRepository;

    public GetMotorcycleByIdUseCase(MotocicletaRepository motocicletaRepository) {
        this.motocicletaRepository = motocicletaRepository;
    }

    @Transactional(readOnly = true)
    public MotocicletaResponse execute(Long id) {
        Motocicleta moto = motocicletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motocicleta não encontrada com ID: " + id));

        return MotocicletaResponse.builder()
                .id(moto.getId())
                .placa(moto.getPlaca())
                .chassi(moto.getChassi())
                .marca(moto.getMarca())
                .modelo(moto.getModelo())
                .ano(moto.getAno())
                .renavam(moto.getRenavam())
                .odometro(moto.getOdometro())
                .dataAquisicao(moto.getDataAquisicao())
                .custoAquisicao(moto.getCustoAquisicao())
                .municipioOperacao(moto.getMunicipioOperacao())
                .statusOperacional(moto.getStatusOperacional().name())
                .statusRastreador(moto.getStatusRastreador().name())
                .criadoEm(moto.getCriadoEm())
                .atualizadoEm(moto.getAtualizadoEm())
                .build();
    }
}

