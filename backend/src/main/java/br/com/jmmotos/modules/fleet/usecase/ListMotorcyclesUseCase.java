package br.com.jmmotos.modules.fleet.usecase;

import br.com.jmmotos.modules.fleet.dto.MotocicletaResponse;
import br.com.jmmotos.modules.fleet.model.Motocicleta;
import br.com.jmmotos.modules.fleet.model.StatusOperacional;
import br.com.jmmotos.modules.fleet.repository.MotocicletaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListMotorcyclesUseCase {

    private final MotocicletaRepository motocicletaRepository;

    public ListMotorcyclesUseCase(MotocicletaRepository motocicletaRepository) {
        this.motocicletaRepository = motocicletaRepository;
    }

    @Transactional(readOnly = true)
    public Page<MotocicletaResponse> execute(String busca, String statusStr, Pageable pageable) {
        StatusOperacional status = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                status = StatusOperacional.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        String termoBusca = (busca != null && !busca.isBlank()) ? busca.trim() : null;
        Page<Motocicleta> page = motocicletaRepository.findWithFilters(termoBusca, status, pageable);
        return page.map(this::mapToResponse);
    }

    private MotocicletaResponse mapToResponse(Motocicleta moto) {
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

