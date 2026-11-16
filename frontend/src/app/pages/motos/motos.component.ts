import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MotorcycleService } from '../../core/services/motorcycle.service';
import { Motocicleta } from '../../core/models/models';

@Component({
  selector: 'app-motos',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="motos-page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Frota de Motocicletas</h1>
          <p class="page-subtitle">Gerenciamento de veículos, odômetro e situação operacional</p>
        </div>
        <button (click)="openCreateModal()" class="btn btn-primary">
          ➕ Nova Motocicleta
        </button>
      </div>

      <!-- Filtro de Categorias / Tags com Rolagem Horizontal (Regra 12) -->
      <div class="tag-scroll scrollbar-hide mb-4">
        <button
          class="tag-item"
          [class.active]="selectedStatus === ''"
          (click)="filterByStatus('')"
        >
          Todas
        </button>
        <button
          class="tag-item"
          [class.active]="selectedStatus === 'DISPONIVEL'"
          (click)="filterByStatus('DISPONIVEL')"
        >
          🟢 Disponíveis
        </button>
        <button
          class="tag-item"
          [class.active]="selectedStatus === 'ALUGADA'"
          (click)="filterByStatus('ALUGADA')"
        >
          🟠 Alugadas
        </button>
        <button
          class="tag-item"
          [class.active]="selectedStatus === 'EM_MANUTENCAO'"
          (click)="filterByStatus('EM_MANUTENCAO')"
        >
          🔧 Em Oficina
        </button>
        <button
          class="tag-item"
          [class.active]="selectedStatus === 'TRANSFERIDA'"
          (click)="filterByStatus('TRANSFERIDA')"
        >
          🏆 Transferidas (Rent-to-Own)
        </button>
      </div>

      <!-- Formulário de Busca com Botões Explícitos (Regra 12) -->
      <div class="card mb-4">
        <div class="search-form">
          <input
            type="text"
            [(ngModel)]="searchQuery"
            (keyup.enter)="search()"
            placeholder="Buscar por placa, modelo ou marca..."
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

      <!-- Tabela de Motocicletas com Skeletons (Regra 12: Preservar Altura) -->
      <div class="card">
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>Placa</th>
                <th>Modelo / Marca</th>
                <th>Ano</th>
                <th>Odômetro</th>
                <th>Status Operacional</th>
                <th>Rastreador GPS</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              @if (loading) {
                @for (item of [1, 2, 3, 4, 5]; track item) {
                  <tr>
                    <td colspan="7">
                      <div class="skeleton skeleton-text" style="width: 100%; height: 24px;"></div>
                    </td>
                  </tr>
                }
              } @else if (motos.length === 0) {
                <tr>
                  <td colspan="7" class="text-center py-6 text-muted">
                    Nenhuma motocicleta encontrada com os filtros informados.
                  </td>
                </tr>
              } @else {
                @for (moto of motos; track moto.id) {
                  <tr>
                    <td>
                      <span class="plate-badge">{{ moto.placa }}</span>
                    </td>
                    <td>
                      <strong>{{ moto.modelo }}</strong>
                      <div class="text-dim text-xs">{{ moto.marca }}</div>
                    </td>
                    <td>{{ moto.ano }}</td>
                    <td>{{ moto.odometro | number }} km</td>
                    <td>
                      <span class="badge" [ngClass]="getStatusBadgeClass(moto.statusOperacional)">
                        {{ moto.statusOperacional }}
                      </span>
                    </td>
                    <td>
                      <span class="badge badge-muted">
                        {{ moto.statusRastreador }}
                      </span>
                    </td>
                    <td>
                      <div class="action-buttons">
                        <button (click)="openStatusModal(moto)" class="btn btn-outline btn-sm">
                          Alterar Status
                        </button>
                      </div>
                    </td>
                  </tr>
                }
              }
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal de Cadastro de Motocicleta -->
      @if (showCreateModal) {
        <div class="modal-backdrop" (click)="closeCreateModal()">
          <div class="modal-dialog" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3 class="card-title">Cadastrar Nova Motocicleta</h3>
              <button (click)="closeCreateModal()" class="btn-close">✕</button>
            </div>
            <form [formGroup]="motoForm" (ngSubmit)="saveMoto()">
              <div class="modal-body">
                @if (formError) {
                  <div class="alert alert-danger">{{ formError }}</div>
                }

                <div class="form-row">
                  <div class="form-group flex-1">
                    <label class="form-label">Placa *</label>
                    <input type="text" formControlName="placa" class="form-control" placeholder="Ex: PHA1234" />
                  </div>
                  <div class="form-group flex-1">
                    <label class="form-label">Chassi *</label>
                    <input type="text" formControlName="chassi" class="form-control" placeholder="17 caracteres" />
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group flex-1">
                    <label class="form-label">Marca *</label>
                    <input type="text" formControlName="marca" class="form-control" placeholder="Honda, Yamaha..." />
                  </div>
                  <div class="form-group flex-1">
                    <label class="form-label">Modelo *</label>
                    <input type="text" formControlName="modelo" class="form-control" placeholder="CG 160 Fan, Factor..." />
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group flex-1">
                    <label class="form-label">Ano *</label>
                    <input type="number" formControlName="ano" class="form-control" placeholder="2024" />
                  </div>
                  <div class="form-group flex-1">
                    <label class="form-label">Odômetro Atual (km) *</label>
                    <input type="number" formControlName="odometro" class="form-control" placeholder="0" />
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group flex-1">
                    <label class="form-label">Custo de Aquisição (R$)</label>
                    <input type="number" formControlName="custoAquisicao" class="form-control" placeholder="16000.00" />
                  </div>
                  <div class="form-group flex-1">
                    <label class="form-label">Município de Operação</label>
                    <input type="text" formControlName="municipioOperacao" class="form-control" placeholder="Manaus ou Uarini" />
                  </div>
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" (click)="closeCreateModal()" class="btn btn-secondary">Cancelar</button>
                <button type="submit" [disabled]="motoForm.invalid || saving" class="btn btn-primary">
                  {{ saving ? 'Salvando...' : 'Cadastrar Motocicleta' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      <!-- Modal de Alteração de Status -->
      @if (selectedMotoForStatus) {
        <div class="modal-backdrop" (click)="selectedMotoForStatus = null">
          <div class="modal-dialog" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3 class="card-title">Atualizar Situação: {{ selectedMotoForStatus.placa }}</h3>
              <button (click)="selectedMotoForStatus = null" class="btn-close">✕</button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="form-label">Novo Status Operacional</label>
                <select [(ngModel)]="newStatus" class="form-control">
                  <option value="DISPONIVEL">🟢 DISPONIVEL (Livre para locação)</option>
                  <option value="EM_MANUTENCAO">🔧 EM MANUTENÇÃO (Oficina)</option>
                  <option value="INATIVA">⛔ INATIVA (Fora de operação temporária)</option>
                </select>
              </div>
            </div>
            <div class="modal-footer">
              <button (click)="selectedMotoForStatus = null" class="btn btn-secondary">Cancelar</button>
              <button (click)="updateStatus()" [disabled]="saving" class="btn btn-primary">
                {{ saving ? 'Atualizando...' : 'Confirmar Novo Status' }}
              </button>
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
      box-shadow: 0 0 10px rgba(0, 245, 160, 0.1);
    }
    .text-dim {
      color: #64748b;
    }
    .text-xs {
      font-size: 0.75rem;
    }
    .text-center {
      text-align: center;
    }
    .py-6 {
      padding-top: 24px;
      padding-bottom: 24px;
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
  `]
})
export class MotosComponent implements OnInit {
  private motoService = inject(MotorcycleService);
  private fb = inject(FormBuilder);

  motos: Motocicleta[] = [];
  loading = false;
  searchQuery = '';
  selectedStatus = '';

  showCreateModal = false;
  selectedMotoForStatus: Motocicleta | null = null;
  newStatus = 'DISPONIVEL';
  saving = false;
  formError = '';

  motoForm = this.fb.group({
    placa: ['', [Validators.required]],
    chassi: ['', [Validators.required]],
    marca: ['Honda', [Validators.required]],
    modelo: ['CG 160 Fan', [Validators.required]],
    ano: [2024, [Validators.required, Validators.min(1990)]],
    odometro: [0, [Validators.required, Validators.min(0)]],
    custoAquisicao: [16000],
    municipioOperacao: ['Manaus']
  });

  ngOnInit(): void {
    this.loadMotos();
  }

  loadMotos(): void {
    this.loading = true;
    this.motoService.list(this.searchQuery, this.selectedStatus).subscribe({
      next: (res) => {
        this.motos = res.content;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  filterByStatus(status: string): void {
    this.selectedStatus = status;
    this.loadMotos();
  }

  search(): void {
    this.loadMotos();
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.selectedStatus = '';
    this.loadMotos();
  }

  openCreateModal(): void {
    this.formError = '';
    this.motoForm.reset({
      marca: 'Honda',
      modelo: 'CG 160 Fan',
      ano: 2024,
      odometro: 0,
      custoAquisicao: 16000,
      municipioOperacao: 'Manaus'
    });
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
  }

  saveMoto(): void {
    if (this.motoForm.invalid) return;

    this.saving = true;
    this.formError = '';

    this.motoService.create(this.motoForm.value as any).subscribe({
      next: () => {
        this.saving = false;
        this.showCreateModal = false;
        this.loadMotos();
      },
      error: (err) => {
        this.saving = false;
        this.formError = err.error?.message || 'Erro ao cadastrar motocicleta.';
      }
    });
  }

  openStatusModal(moto: Motocicleta): void {
    this.selectedMotoForStatus = moto;
    this.newStatus = moto.statusOperacional;
  }

  updateStatus(): void {
    if (!this.selectedMotoForStatus) return;

    this.saving = true;
    this.motoService.updateStatus(this.selectedMotoForStatus.id, this.newStatus).subscribe({
      next: () => {
        this.saving = false;
        this.selectedMotoForStatus = null;
        this.loadMotos();
      },
      error: () => {
        this.saving = false;
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'DISPONIVEL': return 'badge-success';
      case 'ALUGADA': return 'badge-primary';
      case 'EM_MANUTENCAO': return 'badge-warning';
      case 'TRANSFERIDA': return 'badge-info';
      default: return 'badge-muted';
    }
  }
}

