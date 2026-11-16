import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CustomerService } from '../../core/services/customer.service';
import { Cliente } from '../../core/models/models';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="clientes-page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Clientes / Locatários</h1>
          <p class="page-subtitle">Cadastro de locatários, CNH categoria A e proteção de dados (LGPD)</p>
        </div>
        <button (click)="openCreateModal()" class="btn btn-primary">
          ➕ Novo Cliente
        </button>
      </div>

      <!-- Formulário de Busca com Botões Explícitos (Regra 12) -->
      <div class="card mb-4">
        <div class="search-form">
          <input
            type="text"
            [(ngModel)]="searchQuery"
            (keyup.enter)="search()"
            placeholder="Buscar por nome, CPF ou telefone..."
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

      <!-- Tabela com Skeletons (Regra 12) -->
      <div class="card">
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>Nome do Locatário</th>
                <th>CPF Protegido (LGPD)</th>
                <th>CNH / Categoria</th>
                <th>Validade CNH</th>
                <th>Telefone / WhatsApp</th>
                <th>Município</th>
                <th>Status</th>
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
              } @else if (clientes.length === 0) {
                <tr>
                  <td colspan="7" class="text-center py-6 text-muted">
                    Nenhum cliente cadastrado ou encontrado com os filtros atuais.
                  </td>
                </tr>
              } @else {
                @for (c of clientes; track c.id) {
                  <tr>
                    <td>
                      <strong>{{ c.nome }}</strong>
                    </td>
                    <td>
                      <span class="badge badge-muted">{{ c.cpfMascarado || c.cpf }}</span>
                    </td>
                    <td>
                      {{ c.cnhNumero }}
                      <span class="badge badge-primary text-xs">Cat. {{ c.cnhCategoria }}</span>
                    </td>
                    <td>{{ c.cnhValidade }}</td>
                    <td>{{ c.telefone }}</td>
                    <td>{{ c.municipio || 'Manaus' }}</td>
                    <td>
                      <span class="badge badge-success">{{ c.status }}</span>
                    </td>
                  </tr>
                }
              }
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal de Cadastro de Cliente -->
      @if (showCreateModal) {
        <div class="modal-backdrop" (click)="closeCreateModal()">
          <div class="modal-dialog" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3 class="card-title">Cadastrar Novo Locatário</h3>
              <button (click)="closeCreateModal()" class="btn-close">✕</button>
            </div>
            <form [formGroup]="clienteForm" (ngSubmit)="saveCliente()">
              <div class="modal-body">
                @if (formError) {
                  <div class="alert alert-danger">{{ formError }}</div>
                }

                <div class="form-group">
                  <label class="form-label">Nome Completo *</label>
                  <input type="text" formControlName="nome" class="form-control" placeholder="Nome do locatário" />
                </div>

                <div class="form-row">
                  <div class="form-group flex-1">
                    <label class="form-label">CPF *</label>
                    <input type="text" formControlName="cpf" class="form-control" placeholder="000.000.000-00" />
                  </div>
                  <div class="form-group flex-1">
                    <label class="form-label">RG</label>
                    <input type="text" formControlName="rg" class="form-control" placeholder="Número do RG" />
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group flex-1">
                    <label class="form-label">Número da CNH *</label>
                    <input type="text" formControlName="cnhNumero" class="form-control" placeholder="11 dígitos" />
                  </div>
                  <div class="form-group flex-1">
                    <label class="form-label">Validade CNH *</label>
                    <input type="date" formControlName="cnhValidade" class="form-control" />
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group flex-1">
                    <label class="form-label">Telefone / WhatsApp *</label>
                    <input type="text" formControlName="telefone" class="form-control" placeholder="(92) 99999-9999" />
                  </div>
                  <div class="form-group flex-1">
                    <label class="form-label">Município</label>
                    <input type="text" formControlName="municipio" class="form-control" placeholder="Manaus ou Uarini" />
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">Endereço Residencial</label>
                  <input type="text" formControlName="endereco" class="form-control" placeholder="Rua, número, bairro" />
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" (click)="closeCreateModal()" class="btn btn-secondary">Cancelar</button>
                <button type="submit" [disabled]="clienteForm.invalid || saving" class="btn btn-primary">
                  {{ saving ? 'Cadastrando...' : 'Salvar Locatário' }}
                </button>
              </div>
            </form>
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
    .text-center {
      text-align: center;
    }
    .py-6 {
      padding-top: 24px;
      padding-bottom: 24px;
    }
    .text-xs {
      font-size: 0.75rem;
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
export class ClientesComponent implements OnInit {
  private customerService = inject(CustomerService);
  private fb = inject(FormBuilder);

  clientes: Cliente[] = [];
  loading = false;
  searchQuery = '';
  showCreateModal = false;
  saving = false;
  formError = '';

  clienteForm = this.fb.group({
    nome: ['', [Validators.required]],
    cpf: ['', [Validators.required]],
    rg: [''],
    cnhNumero: ['', [Validators.required]],
    cnhCategoria: ['A', [Validators.required]],
    cnhValidade: ['', [Validators.required]],
    telefone: ['', [Validators.required]],
    endereco: [''],
    municipio: ['Manaus']
  });

  ngOnInit(): void {
    this.loadClientes();
  }

  loadClientes(): void {
    this.loading = true;
    this.customerService.list(this.searchQuery).subscribe({
      next: (res) => {
        this.clientes = res.content;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  search(): void {
    this.loadClientes();
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.loadClientes();
  }

  openCreateModal(): void {
    this.formError = '';
    this.clienteForm.reset({
      cnhCategoria: 'A',
      municipio: 'Manaus'
    });
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
  }

  saveCliente(): void {
    if (this.clienteForm.invalid) return;

    this.saving = true;
    this.formError = '';

    this.customerService.create(this.clienteForm.value as any).subscribe({
      next: () => {
        this.saving = false;
        this.showCreateModal = false;
        this.loadClientes();
      },
      error: (err) => {
        this.saving = false;
        this.formError = err.error?.message || 'Erro ao cadastrar cliente.';
      }
    });
  }
}

