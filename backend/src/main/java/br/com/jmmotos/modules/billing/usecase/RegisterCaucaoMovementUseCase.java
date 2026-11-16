package br.com.jmmotos.modules.billing.usecase;

import br.com.jmmotos.exception.BusinessRuleException;
import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.billing.dto.CaucaoMovimentoResponse;
import br.com.jmmotos.modules.billing.dto.RegisterCaucaoRequest;
import br.com.jmmotos.modules.billing.model.CaucaoMovimento;
import br.com.jmmotos.modules.billing.model.TipoMovimentoCaucao;
import br.com.jmmotos.modules.billing.repository.CaucaoMovimentoRepository;
import br.com.jmmotos.modules.rental.model.ContratoLocacao;
import br.com.jmmotos.modules.rental.repository.ContratoLocacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class RegisterCaucaoMovementUseCase {

    private final CaucaoMovimentoRepository caucaoMovimentoRepository;
    private final ContratoLocacaoRepository contratoLocacaoRepository;

    public RegisterCaucaoMovementUseCase(
            CaucaoMovimentoRepository caucaoMovimentoRepository,
            ContratoLocacaoRepository contratoLocacaoRepository) {
        this.caucaoMovimentoRepository = caucaoMovimentoRepository;
        this.contratoLocacaoRepository = contratoLocacaoRepository;
    }

    @Transactional
    public CaucaoMovimentoResponse execute(RegisterCaucaoRequest request, String usuarioAutenticado) {
        ContratoLocacao contrato = contratoLocacaoRepository.findById(request.getContratoId())
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com ID: " + request.getContratoId()));

        TipoMovimentoCaucao tipo;
        try {
            tipo = TipoMovimentoCaucao.valueOf(request.getTipoMovimento().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Tipo de movimento de caução inválido: " + request.getTipoMovimento());
        }

        BigDecimal saldoAtual = caucaoMovimentoRepository.getSaldoCaucaoContrato(contrato.getId());

        if ((tipo == TipoMovimentoCaucao.DEVOLUCAO || tipo == TipoMovimentoCaucao.RETENCAO_DEBITO) &&
                request.getValor().compareTo(saldoAtual) > 0) {
            throw new BusinessRuleException("O valor a deduzir/devolver (R$ " + request.getValor() +
                    ") não pode ser superior ao saldo de caução disponível em custódia (R$ " + saldoAtual + ").");
        }

        CaucaoMovimento movimento = CaucaoMovimento.builder()
                .contrato(contrato)
                .tipoMovimento(tipo)
                .valor(request.getValor())
                .dataMovimento(Instant.now())
                .descricao(request.getDescricao())
                .comprovanteUrl(request.getComprovanteUrl())
                .registradoPor(usuarioAutenticado != null ? usuarioAutenticado : "SISTEMA")
                .build();

        CaucaoMovimento salvo = caucaoMovimentoRepository.save(movimento);
        return mapToResponse(salvo);
    }

    private CaucaoMovimentoResponse mapToResponse(CaucaoMovimento cm) {
        return CaucaoMovimentoResponse.builder()
                .id(cm.getId())
                .contratoId(cm.getContrato().getId())
                .tipoMovimento(cm.getTipoMovimento().name())
                .valor(cm.getValor())
                .dataMovimento(cm.getDataMovimento())
                .descricao(cm.getDescricao())
                .comprovanteUrl(cm.getComprovanteUrl())
                .registradoPor(cm.getRegistradoPor())
                .build();
    }
}

