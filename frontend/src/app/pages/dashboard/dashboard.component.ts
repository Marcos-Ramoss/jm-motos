import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardSummary } from '../../core/models/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard-page">
      <!-- Cabeçalho com Abas Cyber no Estilo do Mockup -->
      <div class="dashboard-top-row">
        <div>
          <div class="cyber-pill">
            <span class="pill-glow-dot"></span>
            <span>Centro de Comando Operacional</span>
          </div>
          <h1 class="page-title">Painel de Controle</h1>
          <p class="page-subtitle">Métricas em tempo real da frota e contas da JM Motos</p>
        </div>
        
        <div class="top-controls">
          <div class="filter-tabs">
            <button class="tab-btn active">Visão Geral</button>
            <button class="tab-btn">Rent-to-Own</button>
            <button class="tab-btn">Financeiro</button>
          </div>
          <button (click)="loadData()" class="btn btn-outline btn-sm" [disabled]="loading">
            <span>🔄</span> Atualizar
          </button>
        </div>
      </div>

      <!-- Alerta GPS Transparente com Borda Cyber -->
      <div class="alert alert-warning cyber-alert">
        <span class="alert-icon">⚠️</span>
        <div class="alert-content">
          <strong>Hardware de Rastreamento GPS Pendente:</strong>
          Nenhum dispositivo físico ou servidor Traccar está conectado no momento. Seguindo os princípios de segurança, nenhuma posição fictícia é gerada.
          <a routerLink="/rastreamento" class="alert-link">Guia de homologação de rastreadores</a>.
        </div>
      </div>

      <!-- Grid de Indicadores Principais (Stat Cards com Efeito Neon) -->
      <div class="stats-grid">
        <!-- Card 1: Frota Total -->
        <div class="card stat-card">
          <div class="stat-header">
            <span class="stat-label">Frota de Motocicletas</span>
            <div class="stat-icon-glow">
              <span>🛵</span>
            </div>
          </div>
          @if (loading) {
            <div class="skeleton skeleton-text" style="width: 50%; height: 32px;"></div>
            <div class="skeleton skeleton-text" style="width: 80%;"></div>
          } @else {
            <div class="stat-value">{{ summary?.totalMotos || 0 }}</div>
            <div class="stat-breakdown">
              <span class="badge badge-success">{{ summary?.motosDisponiveis || 0 }} Livres</span>
              <span class="badge badge-primary">{{ summary?.motosAlugadas || 0 }} Alugadas</span>
              <span class="badge badge-warning">{{ summary?.motosEmManutencao || 0 }} Oficina</span>
            </div>
          }
        </div>

        <!-- Card 2: Contratos Ativos & Rent-to-Own -->
        <div class="card stat-card">
          <div class="stat-header">
            <span class="stat-label">Contratos Ativos</span>
            <div class="stat-icon-glow">
              <span>📑</span>
            </div>
          </div>
          @if (loading) {
            <div class="skeleton skeleton-text" style="width: 50%; height: 32px;"></div>
            <div class="skeleton skeleton-text" style="width: 80%;"></div>
          } @else {
            <div class="stat-value">{{ summary?.contratosAtivos || 0 }}</div>
            <div class="rent-to-own-progress">
              <div class="progress-info">
                <span>Progresso Médio Rent-to-Own:</span>
                <strong>{{ summary?.progressoMedioRentToOwn || 0 }}%</strong>
              </div>
              <div class="progress-container">
                <div class="progress-bar" [style.width.%]="summary?.progressoMedioRentToOwn || 0"></div>
              </div>
            </div>
          }
        </div>

        <!-- Card 3: Receita Realizada (Caixa) -->
        <div class="card stat-card">
          <div class="stat-header">
            <span class="stat-label">Receita Realizada (Caixa)</span>
            <div class="stat-icon-glow">
              <span>💰</span>
            </div>
          </div>
          @if (loading) {
            <div class="skeleton skeleton-text" style="width: 50%; height: 32px;"></div>
            <div class="skeleton skeleton-text" style="width: 80%;"></div>
          } @else {
            <div class="stat-value text-emerald">
              R$ {{ (summary?.totalReceitaRealizada || 0) | number:'1.2-2' }}
            </div>
            <span class="stat-sub">Total confirmado em caixa operacional</span>
          }
        </div>

        <!-- Card 4: A Receber & Atrasos -->
        <div class="card stat-card">
          <div class="stat-header">
            <span class="stat-label">A Receber / Em Atraso</span>
            <div class="stat-icon-glow">
              <span>⏳</span>
            </div>
          </div>
          @if (loading) {
            <div class="skeleton skeleton-text" style="width: 50%; height: 32px;"></div>
            <div class="skeleton skeleton-text" style="width: 80%;"></div>
          } @else {
            <div class="stat-value">
              R$ {{ (summary?.totalAReceber || 0) | number:'1.2-2' }}
            </div>
            <div class="stat-breakdown">
              @if ((summary?.cobrancasAtrasadasCount || 0) > 0) {
                <span class="badge badge-danger">
                  {{ summary?.cobrancasAtrasadasCount }} atrasada(s) (R$ {{ (summary?.totalEmAtraso || 0) | number:'1.2-2' }})
                </span>
              } @else {
                <span class="badge badge-success">✓ Zero inadimplência</span>
              }
            </div>
          }
        </div>
      </div>

      <!-- Gráfico Cyber e Visão Financeira (Inspirado no Bloco do Mockup) -->
      <div class="dashboard-chart-row">
        <!-- Gráfico de Performance Operacional -->
        <div class="card chart-card">
          <div class="card-header">
            <div>
              <h2 class="card-title">📈 Faturamento Operacional Recente</h2>
              <p class="chart-subtitle">Evolução dos recebíveis de locação e taxas de aquisição</p>
            </div>
            <span class="badge badge-success">Semanal</span>
          </div>

          <!-- Gráfico de Barras Estilo Cyber Mockup -->
          <div class="chart-bars-container">
            <div class="bar-col">
              <div class="bar-fill" style="height: 35%;"></div>
              <span class="bar-label">Sem 1</span>
            </div>
            <div class="bar-col">
              <div class="bar-fill" style="height: 55%;"></div>
              <span class="bar-label">Sem 2</span>
            </div>
            <div class="bar-col">
              <div class="bar-fill" style="height: 40%;"></div>
              <span class="bar-label">Sem 3</span>
            </div>
            <div class="bar-col">
              <div class="bar-fill" style="height: 70%;"></div>
              <span class="bar-label">Sem 4</span>
            </div>
            <div class="bar-col">
              <div class="bar-fill" style="height: 85%;"></div>
              <span class="bar-label">Sem 5</span>
            </div>
            <div class="bar-col">
              <div class="bar-fill active" style="height: 100%;"></div>
              <span class="bar-label">Atual</span>
            </div>
          </div>
        </div>

        <!-- Card de Cauções em Custódia Segregada -->
        <div class="card caucao-card">
          <div class="card-header">
            <h2 class="card-title">🛡️ Cauções em Custódia</h2>
            <span class="badge badge-info">Conta Segregada</span>
          </div>
          <p class="section-desc">
            Valores retidos como depósito de garantia contratual. Mantidos em razão separada e <strong>não compõem o faturamento</strong> da empresa.
          </p>
          <div class="caucao-box-glow">
            <div class="caucao-label">Total sob custódia:</div>
            <div class="caucao-amount">
              R$ {{ (summary?.totalCaucaoCustodia || 0) | number:'1.2-2' }}
            </div>
            <div class="caucao-sub">Protegido para restituição ou quitação</div>
          </div>
          <a routerLink="/cobrancas" class="btn btn-outline btn-block">
            Ver Livro Auxiliar de Cauções
          </a>
        </div>
      </div>

      <!-- Atalhos Operacionais -->
      <div class="card quick-actions-card">
        <div class="card-header">
          <h2 class="card-title">⚡ Ações Operacionais Rápidas</h2>
          <span class="badge badge-muted">Atalhos</span>
        </div>
        <div class="quick-actions-grid">
          <a routerLink="/motos" class="action-btn">
            <div class="action-icon-box">🛵</div>
            <div>
              <strong>Cadastrar Motocicleta</strong>
              <small>Adicionar nova unidade à frota</small>
            </div>
          </a>
          <a routerLink="/clientes" class="action-btn">
            <div class="action-icon-box">👤</div>
            <div>
              <strong>Novo Cliente</strong>
              <small>Cadastrar locatário com CNH</small>
            </div>
          </a>
          <a routerLink="/contratos" class="action-btn">
            <div class="action-icon-box">📝</div>
            <div>
              <strong>Emitir Contrato</strong>
              <small>Plano Semanal ou Diário</small>
            </div>
          </a>
          <a routerLink="/cobrancas" class="action-btn">
            <div class="action-icon-box">💵</div>
            <div>
              <strong>Baixar Pagamento</strong>
              <small>Liquidar fatura ou registrar Pix</small>
            </div>
          </a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-top-row {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24px;
      flex-wrap: wrap;
      gap: 16px;
    }

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

    .top-controls {
      display: flex;
      align-items: center;
      gap: 12px;
      flex-wrap: wrap;
    }

    .filter-tabs {
      display: flex;
      background: rgba(12, 20, 24, 0.8);
      border: 1px solid rgba(0, 245, 160, 0.15);
      border-radius: 10px;
      padding: 3px;
      gap: 4px;
    }

    .tab-btn {
      background: transparent;
      border: none;
      color: var(--text-muted);
      padding: 6px 14px;
      border-radius: 7px;
      font-size: 0.8rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.2s ease;
    }

    .tab-btn:hover {
      color: #ffffff;
    }

    .tab-btn.active {
      background: rgba(0, 245, 160, 0.15);
      color: #00f5a0;
      box-shadow: 0 0 10px rgba(0, 245, 160, 0.2);
    }

    .cyber-alert {
      background: rgba(251, 191, 36, 0.05);
      border: 1px solid rgba(251, 191, 36, 0.3);
    }

    .alert-link {
      color: #fbbf24;
      font-weight: 700;
      margin-left: 6px;
      text-decoration: underline;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
      gap: 18px;
      margin-bottom: 24px;
    }

    .stat-card {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      min-height: 150px;
    }

    .stat-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
    }

    .stat-label {
      font-size: 0.78rem;
      font-weight: 700;
      color: var(--text-muted);
      text-transform: uppercase;
      letter-spacing: 0.06em;
    }

    .stat-icon-glow {
      width: 36px;
      height: 36px;
      border-radius: 10px;
      background: rgba(0, 245, 160, 0.08);
      border: 1px solid rgba(0, 245, 160, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
    }

    .stat-value {
      font-size: 2rem;
      font-weight: 800;
      color: #ffffff;
      margin-bottom: 8px;
      letter-spacing: -0.02em;
    }

    .text-emerald {
      color: #00f5a0;
      text-shadow: 0 0 15px rgba(0, 245, 160, 0.25);
    }

    .stat-sub {
      font-size: 0.75rem;
      color: var(--text-dim);
    }

    .stat-breakdown {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
    }

    .rent-to-own-progress {
      margin-top: 6px;
    }

    .progress-info {
      display: flex;
      justify-content: space-between;
      font-size: 0.75rem;
      color: var(--text-muted);
      margin-bottom: 6px;
    }

    /* Linha do Gráfico e Caução */
    .dashboard-chart-row {
      display: grid;
      grid-template-columns: 2fr 1fr;
      gap: 20px;
      margin-bottom: 24px;
    }

    @media (max-width: 992px) {
      .dashboard-chart-row {
        grid-template-columns: 1fr;
      }
    }

    .chart-subtitle {
      font-size: 0.8rem;
      color: var(--text-muted);
      margin-top: 2px;
    }

    .chart-bars-container {
      display: flex;
      align-items: flex-end;
      justify-content: space-between;
      height: 180px;
      padding-top: 20px;
      gap: 16px;
      border-bottom: 1px solid rgba(0, 245, 160, 0.15);
    }

    .bar-col {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      height: 100%;
      justify-content: flex-end;
      gap: 8px;
    }

    .bar-fill {
      width: 100%;
      max-width: 48px;
      background: linear-gradient(180deg, rgba(0, 245, 160, 0.6) 0%, rgba(6, 78, 59, 0.2) 100%);
      border-radius: 6px 6px 0 0;
      border-top: 2px solid #00f5a0;
      transition: all 0.3s ease;
    }

    .bar-fill:hover {
      background: linear-gradient(180deg, #00f5a0 0%, rgba(6, 78, 59, 0.4) 100%);
      box-shadow: 0 0 15px rgba(0, 245, 160, 0.5);
    }

    .bar-fill.active {
      background: linear-gradient(180deg, #00f5a0 0%, rgba(6, 78, 59, 0.4) 100%);
      box-shadow: 0 0 20px rgba(0, 245, 160, 0.6);
      border-top: 3px solid #ffffff;
    }

    .bar-label {
      font-size: 0.75rem;
      color: var(--text-muted);
      font-weight: 600;
    }

    /* Caixa de Caução */
    .section-desc {
      font-size: 0.85rem;
      color: var(--text-muted);
      line-height: 1.5;
      margin-bottom: 18px;
    }

    .caucao-box-glow {
      background: rgba(6, 12, 15, 0.9);
      border: 1px solid rgba(56, 189, 248, 0.25);
      border-radius: 12px;
      padding: 20px;
      margin-bottom: 20px;
      text-align: center;
      box-shadow: inset 0 0 20px rgba(56, 189, 248, 0.05);
    }

    .caucao-label {
      font-size: 0.75rem;
      color: var(--text-muted);
      text-transform: uppercase;
      letter-spacing: 0.05em;
      margin-bottom: 4px;
    }

    .caucao-amount {
      font-size: 2.1rem;
      font-weight: 800;
      color: #38bdf8;
      text-shadow: 0 0 15px rgba(56, 189, 248, 0.3);
    }

    .caucao-sub {
      font-size: 0.75rem;
      color: var(--text-dim);
      margin-top: 4px;
    }

    .btn-block {
      width: 100%;
      text-align: center;
    }

    /* Ações Rápidas */
    .quick-actions-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 14px;
    }

    .action-btn {
      display: flex;
      align-items: center;
      gap: 14px;
      background: rgba(8, 14, 18, 0.7);
      border: 1px solid var(--border-color);
      border-radius: 12px;
      padding: 14px 18px;
      text-decoration: none;
      color: #ffffff;
      transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
    }

    .action-btn:hover {
      background: rgba(0, 245, 160, 0.06);
      border-color: #00f5a0;
      transform: translateY(-2px);
      box-shadow: 0 8px 20px -4px rgba(0, 0, 0, 0.6), 0 0 15px rgba(0, 245, 160, 0.2);
    }

    .action-icon-box {
      width: 42px;
      height: 42px;
      border-radius: 10px;
      background: rgba(0, 245, 160, 0.1);
      border: 1px solid rgba(0, 245, 160, 0.25);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      flex-shrink: 0;
    }

    .action-btn strong {
      display: block;
      font-size: 0.9rem;
      font-weight: 700;
    }

    .action-btn small {
      color: var(--text-muted);
      font-size: 0.75rem;
    }
  `]
})
export class DashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);

  summary: DashboardSummary | null = null;
  loading = false;

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.dashboardService.getSummary().subscribe({
      next: (res) => {
        this.summary = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
