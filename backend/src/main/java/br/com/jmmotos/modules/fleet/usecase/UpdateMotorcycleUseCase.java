package br.com.jmmotos.modules.fleet.usecase;

import br.com.jmmotos.exception.ConflictException;
import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.fleet.dto.MotocicletaRequest;
import br.com.jmmotos.modules.fleet.dto.MotocicletaResponse;
import br.com.jmmotos.modules.fleet.model.Motocicleta;
import br.com.jmmotos.modules.fleet.repository.MotocicletaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateMotorcycleUseCase {

    private final MotocicletaRepository motocicletaRepository;

    public UpdateMotorcycleUseCase(MotocicletaRepository motocicletaRepository) {
        this.motocicletaRepository = motocicletaRepository;
    }

    @Transactional
    public MotocicletaResponse execute(Long id, MotocicletaRequest request) {
        Motocicleta moto = motocicletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motocicleta não encontrada com ID: " + id));

        String placaFormatada = request.getPlaca().trim().toUpperCase().replace("-", "");
        String chassiFormatado = request.getChassi().trim().toUpperCase();

        if (!moto.getPlaca().equals(placaFormatada) && motocicletaRepository.existsByPlaca(placaFormatada)) {
            throw new ConflictException("Já existe outra motocicleta com a placa: " + placaFormatada);
        }
        if (!moto.getChassi().equals(chassiFormatado) && motocicletaRepository.existsByChassi(chassiFormatado)) {
            throw new ConflictException("Já existe outra motocicleta com o chassi: " + chassiFormatado);
        }

        moto.setPlaca(placaFormatada);
        moto.setChassi(chassiFormatado);
        moto.setMarca(request.getMarca().trim());
        moto.setModelo(request.getModelo().trim());
        moto.setAno(request.getAno());
        moto.setRenavam(request.getRenavam() != null ? request.getRenavam().trim() : null);
        moto.setOdometro(request.getOdometro());
        moto.setDataAquisicao(request.getDataAquisicao());
        moto.setCustoAquisicao(request.getCustoAquisicao());
        moto.setMunicipioOperacao(request.getMunicipioOperacao() != null ? request.getMunicipioOperacao().trim() : "Manaus");

        Motocicleta atualizada = motocicletaRepository.save(moto);
        return mapToResponse(atualizada);
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

