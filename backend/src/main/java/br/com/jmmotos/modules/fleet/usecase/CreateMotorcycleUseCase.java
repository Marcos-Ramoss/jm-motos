package br.com.jmmotos.modules.fleet.usecase;

import br.com.jmmotos.exception.ConflictException;
import br.com.jmmotos.modules.fleet.dto.MotocicletaRequest;
import br.com.jmmotos.modules.fleet.dto.MotocicletaResponse;
import br.com.jmmotos.modules.fleet.model.Motocicleta;
import br.com.jmmotos.modules.fleet.model.StatusOperacional;
import br.com.jmmotos.modules.fleet.model.StatusRastreador;
import br.com.jmmotos.modules.fleet.repository.MotocicletaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateMotorcycleUseCase {

    private final MotocicletaRepository motocicletaRepository;

    public CreateMotorcycleUseCase(MotocicletaRepository motocicletaRepository) {
        this.motocicletaRepository = motocicletaRepository;
    }

    @Transactional
    public MotocicletaResponse execute(MotocicletaRequest request) {
        String placaFormatada = request.getPlaca().trim().toUpperCase().replace("-", "");
        String chassiFormatado = request.getChassi().trim().toUpperCase();

        if (motocicletaRepository.existsByPlaca(placaFormatada)) {
            throw new ConflictException("Já existe uma motocicleta cadastrada com a placa: " + placaFormatada);
        }
        if (motocicletaRepository.existsByChassi(chassiFormatado)) {
            throw new ConflictException("Já existe uma motocicleta cadastrada com o chassi: " + chassiFormatado);
        }

        Motocicleta moto = Motocicleta.builder()
                .placa(placaFormatada)
                .chassi(chassiFormatado)
                .marca(request.getMarca().trim())
                .modelo(request.getModelo().trim())
                .ano(request.getAno())
                .renavam(request.getRenavam() != null ? request.getRenavam().trim() : null)
                .odometro(request.getOdometro())
                .dataAquisicao(request.getDataAquisicao())
                .custoAquisicao(request.getCustoAquisicao())
                .municipioOperacao(request.getMunicipioOperacao() != null ? request.getMunicipioOperacao().trim() : "Manaus")
                .statusOperacional(StatusOperacional.DISPONIVEL)
                .statusRastreador(StatusRastreador.NAO_VINCULADO)
                .build();

        Motocicleta salva = motocicletaRepository.save(moto);
        return mapToResponse(salva);
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

