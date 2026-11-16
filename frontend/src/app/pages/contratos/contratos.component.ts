import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ContractService } from '../../core/services/contract.service';
import { MotorcycleService } from '../../core/services/motorcycle.service';
import { CustomerService } from '../../core/services/customer.service';
import { Cliente, Contrato, Motocicleta, Plano } from '../../core/models/models';

@Component({
  selector: 'app-contratos',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="contratos-page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Contratos de Locação (Rent-to-Own)</h1>
          <p class="page-subtitle">Emissão de contratos, snapshot de condições imutáveis e acompanhamento de compra</p>
        </div>
        <button (click)="openCreateModal()" class="btn btn-primary">
          📝 Novo Contrato
        </button>
      </div>

      <!-- Formulário de Busca com Botões Explícitos (Regra 12) -->
      <div class="card mb-4">
        <div class="search-form">
          <input
            type="text"
            [(ngModel)]="searchQuery"
            (keyup.enter)="search()"
            placeholder="Buscar por número do contrato, cliente ou placa..."
            class="form-control"
          />
          <div class="search-actions">
            <button (click)="search()" class="btn btn-primary">
              🔍 Pesquisar
            </button>
            <button (click)="clearSearch()" class="btn btn-secondary">
              🧹 Limpar
            </button>
          </div>
        </div>
      </div>

      <!-- Tabela de Contratos -->
      <div class="card">
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>Nº Contrato</th>
                <th>Cliente Locatário</th>
                <th>Motocicleta</th>
                <th>Modalidade</th>
                <th>Valor Parcela</th>
                <th>Progresso Rent-to-Own</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              @if (loading) {
                @for (item of [1, 2, 3, 4, 5]; track item) {
                  <tr>
                    <td colspan="8">
                      <div class="skeleton skeleton-text" style="width: 100%; height: 24px;"></div>
                    </td>
                  </tr>
                }
              } @else if (contratos.length === 0) {
                <tr>
                  <td colspan="8" class="text-center py-6 text-muted">
                    Nenhum contrato encontrado.
                  </td>
                </tr>
              } @else {
                @for (c of contratos; track c.id) {
                  <tr>
                    <td>
                      <strong>{{ c.numeroContrato }}</strong>
                      <div class="text-dim text-xs">Desde {{ c.dataInicio }}</div>
                    </td>
                    <td>
                      {{ c.clienteNome }}
                    </td>
                    <td>
                      <span class="plate-badge">{{ c.motocicletaPlaca }}</span>
                      <div class="text-dim text-xs">{{ c.motocicletaModelo }}</div>
                    </td>
                    <td>
                      <span class="badge" [ngClass]="c.snapshotModalidade === 'DIARIO' ? 'badge-primary' : 'badge-info'">
                        {{ c.snapshotModalidade }}
                      </span>
                    </td>
                    <td>
                      <strong>R$ {{ c.snapshotValorParcela | number:'1.2-2' }}</strong>
                    </td>
                    <td style="min-width: 180px;">
                      <div class="progress-info">
                        <span>{{ c.unidadesQuitadas }} / {{ c.unidadesTotais }}</span>
                        <strong>{{ c.percentualAquisicao }}%</strong>
                      </div>
                      <div class="progress-container">
                        <div class="progress-bar" [style.width.%]="c.percentualAquisicao"></div>
                      </div>
                    </td>
                    <td>
                      <span class="badge" [ngClass]="getStatusBadgeClass(c.status)">
                        {{ c.status }}
                      </span>
                    </td>
                    <td>
                      <button (click)="viewDetails(c)" class="btn btn-outline btn-sm">
                        Detalhes
                      </button>
                    </td>
                  </tr>
                }
              }
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal de Criação de Contrato -->
      @if (showCreateModal) {
        <div class="modal-backdrop" (click)="closeCreateModal()">
          <div class="modal-dialog" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3 class="card-title">Emitir Novo Contrato de Locação</h3>
              <button (click)="closeCreateModal()" class="btn-close">✕</button>
            </div>
            <form [formGroup]="contractForm" (ngSubmit)="saveContract()">
              <div class="modal-body">
                @if (formError) {
                  <div class="alert alert-danger">{{ formError }}</div>
                }

                <div class="form-group">
                  <label class="form-label">Selecione o Cliente *</label>
                  <select formControlName="clienteId" class="form-control">
                    <option value="">Selecione um cliente ativo...</option>
                    @for (cli of clientesDisponiveis; track cli.id) {
                      <option [value]="cli.id">{{ cli.nome }} (CPF: {{ cli.cpfMascarado || cli.cpf }})</option>
                    }
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label">Selecione a Motocicleta (Disponível) *</label>
                  <select formControlName="motocicletaId" class="form-control">
                    <option value="">Selecione uma moto livre...</option>
                    @for (m of motosDisponiveis; track m.id) {
                      <option [value]="m.id">{{ m.placa }} - {{ m.modelo }} ({{ m.ano }})</option>
                    }
                  </select>
                  @if (motosDisponiveis.length === 0) {
                    <small class="text-warning">Não há motocicletas com status DISPONÍVEL no momento.</small>
                  }
                </div>

                <div class="form-group">
                  <label class="form-label">Modelo de Plano Contratual *</label>
                  <select formControlName="planoVersaoId" class="form-control">
                    <option value="">Selecione o plano...</option>
                    @for (p of planos; track p.id) {
                      @for (v of p.versoes; track v.id) {
                        <option [value]="v.id">
                          {{ p.nome }} ({{ v.modalidade }} - R$ {{ v.valorParcela }}/unidade - Caução R$ {{ v.valorCaucao }})
                        </option>
                      }
                    }
                  </select>
                </div>

                <div class="form-row">
                  <div class="form-group flex-1">
                    <label class="form-label">Data de Início da Locação *</label>
                    <input type="date" formControlName="dataInicio" class="form-control" />
                  </div>
                  <div class="form-group flex-1">
                    <label class="form-label">Dia de Vencimento Padrão</label>
                    <input type="number" formControlName="diaVencimentoPersonalizado" class="form-control" placeholder="1 a 31" />
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">Observações</label>
                  <textarea formControlName="observacoes" class="form-control" rows="2" placeholder="Acordos particulares ou dados de entrega..."></textarea>
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" (click)="closeCreateModal()" class="btn btn-secondary">Cancelar</button>
                <button type="submit" [disabled]="contractForm.invalid || saving" class="btn btn-primary">
                  {{ saving ? 'Emitindo Contrato...' : 'Emitir e Salvar Contrato' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      <!-- Modal de Detalhes do Contrato -->
      @if (selectedContratoDetail) {
        <div class="modal-backdrop" (click)="selectedContratoDetail = null">
          <div class="modal-dialog modal-lg" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <div>
                <h3 class="card-title">Contrato: {{ selectedContratoDetail.numeroContrato }}</h3>
                <span class="badge" [ngClass]="getStatusBadgeClass(selectedContratoDetail.status)">
                  {{ selectedContratoDetail.status }}
                </span>
              </div>
              <button (click)="selectedContratoDetail = null" class="btn-close">✕</button>
            </div>
            <div class="modal-body">
              <!-- Snapshot Imutável -->
              <div class="card mb-4" style="background: #0f172a;">
                <h4 class="text-sm font-bold text-muted uppercase mb-2">Snapshot Imutável Assinado</h4>
                <div class="snapshot-grid">
                  <div>
                    <small class="text-dim">Modalidade:</small>
                    <div><strong>{{ selectedContratoDetail.snapshotModalidade }}</strong></div>
                  </div>
                  <div>
                    <small class="text-dim">Valor da Parcela:</small>
                    <div><strong>R$ {{ selectedContratoDetail.snapshotValorParcela | number:'1.2-2' }}</strong></div>
                  </div>
                  <div>
                    <small class="text-dim">Caução de Garantia:</small>
                    <div><strong>R$ {{ selectedContratoDetail.snapshotValorCaucao | number:'1.2-2' }}</strong></div>
                  </div>
                  <div>
                    <small class="text-dim">Total Unidades:</small>
                    <div><strong>{{ selectedContratoDetail.snapshotTotalUnidades }} unidades</strong></div>
                  </div>
                </div>
              </div>

              <!-- Progresso Rent-to-Own -->
              <div class="card mb-4">
                <h4 class="card-title text-sm mb-2">Progresso Rent-to-Own</h4>
                <div class="progress-info">
                  <span>{{ selectedContratoDetail.unidadesQuitadas }} unidades integralmente quitadas</span>
                  <strong>{{ selectedContratoDetail.percentualAquisicao }}%</strong>
                </div>
                <div class="progress-container">
                  <div class="progress-bar" [style.width.%]="selectedContratoDetail.percentualAquisicao"></div>
                </div>
              </div>

              <!-- Saldo de Caução -->
              <div class="card mb-4">
                <div class="flex justify-between items-center">
                  <div>
                    <h4 class="card-title text-sm">Saldo de Caução em Custódia:</h4>
                    <span class="text-dim text-xs">Mantido em razão separada da receita</span>
                  </div>
                  <div class="text-info font-bold text-lg">
                    R$ {{ (selectedContratoDetail.saldoCaucaoAtual || 0) | number:'1.2-2' }}
                  </div>
                </div>
              </div>

              <!-- Tabela de Cobranças do Contrato -->
              <h4 class="card-title text-sm mb-2">Cobranças Geradas</h4>
              <div class="table-responsive">
                <table class="table">
                  <thead>
                    <tr>
                      <th>Parcela</th>
                      <th>Vencimento</th>
                      <th>Valor</th>
                      <th>Pago</th>
                      <th>Saldo</th>
                      <th>Situação</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (cob of selectedContratoDetail.cobrancas; track cob.id) {
                      <tr>
                        <td>#{{ cob.numeroParcela }}</td>
                        <td>{{ cob.dataVencimento }}</td>
                        <td>R$ {{ cob.valorTotal | number:'1.2-2' }}</td>
                        <td>R$ {{ cob.valorPago | number:'1.2-2' }}</td>
                        <td>R$ {{ cob.saldoRestante | number:'1.2-2' }}</td>
                        <td>
                          <span class="badge" [ngClass]="getCobrancaStatusBadgeClass(cob.status)">
                            {{ cob.status }}
                          </span>
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            </div>
            <div class="modal-footer">
              @if (selectedContratoDetail.status === 'ATIVO') {
                <button (click)="cancelContract(selectedContratoDetail.id)" class="btn btn-danger btn-sm">
                  Cancelar Contrato & Liberar Moto
                </button>
              }
              <button (click)="selectedContratoDetail = null" class="btn btn-secondary">Fechar</button>
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      flex-wrap: wrap;
      gap: 12px;
    }
    .page-title {
      font-size: 1.5rem;
      font-weight: 800;
      color: #f8fafc;
    }
    .page-subtitle {
      font-size: 0.85rem;
      color: #94a3b8;
    }
    .search-form {
      display: flex;
      gap: 12px;
      align-items: center;
    }
    @media (max-width: 600px) {
      .search-form {
        flex-direction: column;
        align-items: stretch;
      }
      .search-actions {
        display: flex;
        gap: 8px;
      }
      .search-actions button {
        flex: 1;
      }
    }
    .search-actions {
      display: flex;
      gap: 8px;
    }
    .mb-4 {
      margin-bottom: 16px;
    }
    .mb-2 {
      margin-bottom: 8px;
    }
    .plate-badge {
      background: rgba(0, 245, 160, 0.06);
      border: 1px solid rgba(0, 245, 160, 0.3);
      color: #00f5a0;
      padding: 4px 10px;
      border-radius: 6px;
      font-weight: 800;
      font-family: monospace;
      font-size: 0.9rem;
    }
    .text-dim {
      color: #64748b;
    }
    .text-xs {
      font-size: 0.75rem;
    }
    .text-sm {
      font-size: 0.875rem;
    }
    .text-lg {
      font-size: 1.25rem;
    }
    .text-info {
      color: #38bdf8;
    }
    .text-warning {
      color: #f59e0b;
    }
    .text-center {
      text-align: center;
    }
    .py-6 {
      padding-top: 24px;
      padding-bottom: 24px;
    }
    .font-bold {
      font-weight: 700;
    }
    .uppercase {
      text-transform: uppercase;
    }
    .flex {
      display: flex;
    }
    .justify-between {
      justify-content: space-between;
    }
    .items-center {
      align-items: center;
    }
    .snapshot-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
      gap: 12px;
      margin-top: 8px;
    }
    .form-row {
      display: flex;
      gap: 12px;
    }
    @media (max-width: 500px) {
      .form-row {
        flex-direction: column;
        gap: 0;
      }
    }
    .flex-1 {
      flex: 1;
    }
    .btn-close {
      background: transparent;
      border: none;
      color: #94a3b8;
      font-size: 1.1rem;
      cursor: pointer;
    }
    .modal-lg {
      max-width: 800px;
    }
  `]
})
export class ContratosComponent implements OnInit {
  private contractService = inject(ContractService);
  private motoService = inject(MotorcycleService);
  private customerService = inject(CustomerService);
  private fb = inject(FormBuilder);

  contratos: Contrato[] = [];
  planos: Plano[] = [];
  motosDisponiveis: Motocicleta[] = [];
  clientesDisponiveis: Cliente[] = [];

  loading = false;
  searchQuery = '';
  showCreateModal = false;
  selectedContratoDetail: Contrato | null = null;
  saving = false;
  formError = '';

  contractForm = this.fb.group({
    clienteId: ['', [Validators.required]],
    motocicletaId: ['', [Validators.required]],
    planoVersaoId: ['', [Validators.required]],
    dataInicio: [new Date().toISOString().substring(0, 10), [Validators.required]],
    diaVencimentoPersonalizado: [1],
    observacoes: ['']
  });

  ngOnInit(): void {
    this.loadContratos();
  }

  loadContratos(): void {
    this.loading = true;
    this.contractService.list(this.searchQuery).subscribe({
      next: (res) => {
        this.contratos = res.content;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  search(): void {
    this.loadContratos();
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.loadContratos();
  }

  openCreateModal(): void {
    this.formError = '';
    this.contractForm.reset({
      dataInicio: new Date().toISOString().substring(0, 10),
      diaVencimentoPersonalizado: 1
    });

    // Carrega dados necessários para o formulário
    this.contractService.listPlans().subscribe(p => this.planos = p);
    this.motoService.list(undefined, 'DISPONIVEL', 0, 100).subscribe(m => this.motosDisponiveis = m.content);
    this.customerService.list(undefined, 'ATIVO', 0, 100).subscribe(c => this.clientesDisponiveis = c.content);

    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
  }

  saveContract(): void {
    if (this.contractForm.invalid) return;

    this.saving = true;
    this.formError = '';

    const val = this.contractForm.value;
    const payload = {
      clienteId: Number(val.clienteId),
      motocicletaId: Number(val.motocicletaId),
      planoVersaoId: Number(val.planoVersaoId),
      dataInicio: val.dataInicio!,
      diaVencimentoPersonalizado: val.diaVencimentoPersonalizado ? Number(val.diaVencimentoPersonalizado) : undefined,
      observacoes: val.observacoes || undefined
    };

    this.contractService.create(payload).subscribe({
      next: () => {
        this.saving = false;
        this.showCreateModal = false;
        this.loadContratos();
      },
      error: (err) => {
        this.saving = false;
        this.formError = err.error?.message || 'Erro ao emitir contrato.';
      }
    });
  }

  viewDetails(contrato: Contrato): void {
    this.contractService.getById(contrato.id).subscribe({
      next: (res) => {
        this.selectedContratoDetail = res;
      }
    });
  }

  cancelContract(id: number): void {
    if (!confirm('Deseja realmente cancelar este contrato? A motocicleta será liberada de volta para a frota.')) return;

    this.contractService.cancel(id, 'Cancelado pelo operador').subscribe({
      next: () => {
        this.selectedContratoDetail = null;
        this.loadContratos();
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'ATIVO': return 'badge-success';
      case 'CONCLUIDO_AGUARDANDO_TRANSFERENCIA': return 'badge-info';
      case 'TRANSFERIDO': return 'badge-primary';
      case 'CANCELADO': return 'badge-danger';
      default: return 'badge-muted';
    }
  }

  getCobrancaStatusBadgeClass(status: string): string {
    switch (status) {
      case 'QUITADO': return 'badge-success';
      case 'PAGO_PARCIAL': return 'badge-warning';
      case 'ISENTO': return 'badge-info';
      case 'ATRASADO': return 'badge-danger';
      default: return 'badge-muted';
    }
  }
}

