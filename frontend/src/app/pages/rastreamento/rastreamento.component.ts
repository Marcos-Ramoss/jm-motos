import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TrackingService } from '../../core/services/tracking.service';
import { TrackingStatus } from '../../core/models/models';

@Component({
  selector: 'app-rastreamento',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rastreamento-page">
      <div class="page-header">
        <div>
          <div class="cyber-pill">
            <span class="pill-glow-dot"></span>
            <span>Módulo de Telemetria e Localização</span>
          </div>
          <h1 class="page-title">Rastreamento GPS da Frota</h1>
          <p class="page-subtitle">Monitoramento de conectividade de rastreadores físicos e integrador Traccar</p>
        </div>
      </div>

      <!-- Radar Cyber Grid inspirado no globo da imagem de referência -->
      <div class="radar-card mb-6">
        <div class="radar-visual">
          <div class="radar-circle radar-outer"></div>
          <div class="radar-circle radar-mid"></div>
          <div class="radar-circle radar-inner"></div>
          <div class="radar-sweep"></div>
          <div class="radar-center-dot"></div>
          <div class="radar-satellite-tag">Satélites GPS: Standby</div>
        </div>
        <div class="radar-info">
          <div class="flex items-center gap-2 mb-2">
            <span class="badge badge-warning">Aguardando Hardware</span>
            <span class="badge badge-muted">Protocolo Traccar</span>
          </div>
          <h2 class="card-title text-xl text-white">Status da Conexão: Provedor Não Configurado</h2>
          <p class="text-muted text-sm mb-4">
            {{ trackingData?.mensagem }}
          </p>
          <div class="alert alert-info mb-0">
            <strong>Instruções de Pareamento Técnico:</strong>
            <p class="text-xs mt-1">
              {{ trackingData?.instrucoesIntegracao }}
            </p>
          </div>
        </div>
      </div>

      <!-- Lista de Veículos e Situação de Telemetria -->
      <div class="card mb-6">
        <div class="card-header">
          <div>
            <h3 class="card-title">Dispositivos Vinculados à Frota</h3>
            <p class="text-dim text-xs">Motos registradas no sistema com checagem de telemetria</p>
          </div>
          <span class="badge badge-muted">0 Dispositivos Pareados</span>
        </div>
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>Placa</th>
                <th>Modelo</th>
                <th>Situação Operacional</th>
                <th>Sinal do Rastreador</th>
                <th>Última Transmissão</th>
                <th>Coordenadas Geográficas</th>
              </tr>
            </thead>
            <tbody>
              @if (loading) {
                @for (item of [1, 2, 3]; track item) {
                  <tr>
                    <td colspan="6">
                      <div class="skeleton skeleton-text" style="width: 100%; height: 24px;"></div>
                    </td>
                  </tr>
                }
              } @else if (!trackingData?.motocicletas?.length) {
                <tr>
                  <td colspan="6" class="text-center py-6 text-muted">
                    Nenhuma motocicleta vinculada no momento.
                  </td>
                </tr>
              } @else {
                @for (m of trackingData?.motocicletas; track m.motoId) {
                  <tr>
                    <td>
                      <span class="plate-badge">{{ m.placa }}</span>
                    </td>
                    <td>{{ m.modelo }}</td>
                    <td>
                      <span class="badge badge-muted">{{ m.statusOperacional }}</span>
                    </td>
                    <td>
                      <span class="badge badge-warning">
                        {{ m.statusRastreador }}
                      </span>
                    </td>
                    <td class="text-dim text-xs">
                      {{ m.ultimaComunicacao }}
                    </td>
                    <td class="text-dim text-xs">
                      <em>Sem coordenadas simuladas (Segurança)</em>
                    </td>
                  </tr>
                }
              }
            </tbody>
          </table>
        </div>
      </div>

      <!-- Nota de Segurança e LGPD -->
      <div class="card legal-card">
        <h4 class="text-sm font-bold text-emerald uppercase mb-2">🔒 Segurança Viária, Limites Legais e LGPD</h4>
        <ul class="legal-notes text-xs text-muted">
          <li><strong>Bloqueio remoto com veículo em trânsito é estritamente proibido:</strong> A aplicação não executa corte de ignição ou parada súbita de motor por atrasos financeiros ou saída de cercas geográficas, em cumprimento às normas de segurança viária.</li>
          <li><strong>Minimização de Dados (LGPD):</strong> O histórico de trajetos constitui dado sensível e será visível apenas por operadores credenciados mediante autenticação forte.</li>
          <li><strong>Auditoria em Nuvem:</strong> Toda consulta de posicionamento registrará carimbo de data/hora UTC e a identificação do operador requisitante.</li>
        </ul>
      </div>
    </div>
  `,
  styles: [`
    .cyber-pill {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 4px 12px;
      border-radius: 9999px;
      background: rgba(0, 245, 160, 0.08);
      border: 1px solid rgba(0, 245, 160, 0.25);
      color: #00f5a0;
      font-size: 0.72rem;
      font-weight: 700;
      letter-spacing: 0.05em;
      text-transform: uppercase;
      margin-bottom: 8px;
    }

    .pill-glow-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: #00f5a0;
      box-shadow: 0 0 8px #00f5a0;
    }

    /* Radar Card estilo Cyber Grid */
    .radar-card {
      background: rgba(10, 18, 20, 0.85);
      backdrop-filter: blur(16px);
      border: 1px solid rgba(0, 245, 160, 0.25);
      border-radius: var(--radius);
      padding: 28px;
      box-shadow: 0 16px 40px rgba(0, 0, 0, 0.7), 0 0 30px rgba(0, 245, 160, 0.1);
      display: flex;
      align-items: center;
      gap: 32px;
      flex-wrap: wrap;
      position: relative;
      overflow: hidden;
    }

    .radar-visual {
      width: 180px;
      height: 180px;
      border-radius: 50%;
      background: rgba(0, 245, 160, 0.03);
      border: 1px solid rgba(0, 245, 160, 0.3);
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      box-shadow: inset 0 0 30px rgba(0, 245, 160, 0.1), 0 0 20px rgba(0, 245, 160, 0.15);
    }

    .radar-circle {
      position: absolute;
      border-radius: 50%;
      border: 1px dashed rgba(0, 245, 160, 0.25);
    }

    .radar-outer {
      width: 140px;
      height: 140px;
    }

    .radar-mid {
      width: 90px;
      height: 90px;
    }

    .radar-inner {
      width: 40px;
      height: 40px;
    }

    .radar-center-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #00f5a0;
      box-shadow: 0 0 12px #00f5a0;
    }

    .radar-sweep {
      position: absolute;
      width: 50%;
      height: 50%;
      top: 0;
      right: 0;
      background: linear-gradient(45deg, rgba(0, 245, 160, 0.25), transparent);
      transform-origin: bottom left;
      animation: radar-spin 4s linear infinite;
      border-radius: 100% 0 0 0;
    }

    @keyframes radar-spin {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }

    .radar-satellite-tag {
      position: absolute;
      bottom: -6px;
      background: rgba(4, 8, 10, 0.9);
      border: 1px solid rgba(0, 245, 160, 0.3);
      padding: 2px 8px;
      border-radius: 9999px;
      font-size: 0.65rem;
      color: #00f5a0;
      font-weight: 700;
    }

    .radar-info {
      flex: 1;
      min-width: 280px;
    }

    .text-emerald {
      color: #00f5a0;
    }

    .plate-badge {
      background: rgba(0, 245, 160, 0.06);
      border: 1px solid rgba(0, 245, 160, 0.3);
      color: #00f5a0;
      padding: 4px 10px;
      border-radius: 6px;
      font-weight: 800;
      letter-spacing: 0.08em;
      font-family: monospace;
      font-size: 0.9rem;
    }

    .legal-card {
      background: rgba(8, 14, 16, 0.75);
      border-left: 4px solid #00f5a0;
    }

    .legal-notes {
      padding-left: 20px;
      line-height: 1.8;
    }

    .legal-notes li {
      margin-bottom: 6px;
    }

    .mb-6 { margin-bottom: 24px; }
    .mb-4 { margin-bottom: 16px; }
    .mb-2 { margin-bottom: 8px; }
    .mb-0 { margin-bottom: 0; }
    .mt-1 { margin-top: 4px; }
    .flex { display: flex; }
    .items-center { align-items: center; }
    .gap-2 { gap: 8px; }
    .text-sm { font-size: 0.875rem; }
    .text-xs { font-size: 0.75rem; }
    .text-xl { font-size: 1.25rem; font-weight: 700; }
    .text-white { color: #ffffff; }
    .text-muted { color: var(--text-muted); }
    .font-bold { font-weight: 700; }
    .uppercase { text-transform: uppercase; }
    .text-center { text-align: center; }
    .py-6 { padding-top: 24px; padding-bottom: 24px; }
  `]
})
export class RastreamentoComponent implements OnInit {
  private trackingService = inject(TrackingService);

  trackingData: TrackingStatus | null = null;
  loading = false;

  ngOnInit(): void {
    this.loadStatus();
  }

  loadStatus(): void {
    this.loading = true;
    this.trackingService.getStatus().subscribe({
      next: (res) => {
        this.trackingData = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
