import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { BillingService } from '../../core/services/billing.service';
import { CaucaoMovimento, Cobranca, Pagamento } from '../../core/models/models';

@Component({
  selector: 'app-cobrancas',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="cobrancas-page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Cobranças & Contas a Receber</h1>
          <p class="page-subtitle">Baixa manual de diárias e semanas, pagamentos parciais, isenções por oficina e cauções</p>
        </div>
        <div class="header-actions">
          <button (click)="openCaucaoModal()" class="btn btn-outline btn-sm">
            🛡️ Movimentar Caução
          </button>
        </div>
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
          [class.active]="selectedStatus === 'PENDENTE'"
          (click)="filterByStatus('PENDENTE')"
        >
          🟡 Pendentes
        </button>
        <button
          class="tag-item"
          [class.active]="selectedStatus === 'PAGO_PARCIAL'"
          (click)="filterByStatus('PAGO_PARCIAL')"
        >
          🟠 Parcialmente Pagas
        </button>
        <button
          class="tag-item"
          [class.active]="selectedStatus === 'QUITADO'"
          (click)="filterByStatus('QUITADO')"
        >
          🟢 Quitadas
        </button>
        <button
          class="tag-item"
          [class.active]="selectedStatus === 'ATRASADO'"
          (click)="filterByStatus('ATRASADO')"
        >
          🔴 Em Atraso
        </button>
        <button
          class="tag-item"
          [class.active]="selectedStatus === 'ISENTO'"
          (click)="filterByStatus('ISENTO')"
        >
          🔧 Isenção Oficina
        </button>
      </div>

      <!-- Formulário de Filtro com Botões Explícitos (Regra 12) -->
      <div class="card mb-4">
        <div class="filter-grid">
          <div class="form-group mb-0">
            <label class="form-label text-xs">Data Início</label>
            <input type="date" [(ngModel)]="dataInicio" class="form-control" />
          </div>
          <div class="form-group mb-0">
            <label class="form-label text-xs">Data Fim</label>
            <input type="date" [(ngModel)]="dataFim" class="form-control" />
          </div>
          <div class="filter-actions">
            <button (click)="loadCobrancas()" class="btn btn-primary btn-sm">
              🔍 Filtrar
            </button>
            <button (click)="clearFilters()" class="btn btn-secondary btn-sm">
              🧹 Limpar
            </button>
          </div>
        </div>
      </div>

      <!-- Tabela de Cobranças -->
      <div class="card mb-6">
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>Vencimento</th>
                <th>Contrato / Cliente</th>
                <th>Parcela</th>
                <th>Valor Total</th>
                <th>Valor Pago</th>
                <th>Saldo Devedor</th>
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
              } @else if (cobrancas.length === 0) {
                <tr>
                  <td colspan="8" class="text-center py-6 text-muted">
                    Nenhuma cobrança encontrada com os filtros selecionados.
                  </td>
                </tr>
              } @else {
                @for (cob of cobrancas; track cob.id) {
                  <tr>
                    <td>
                      <strong>{{ cob.dataVencimento }}</strong>
                    </td>
                    <td>
                      <div><strong>{{ cob.clienteNome }}</strong></div>
                      <div class="text-dim text-xs">{{ cob.numeroContrato }} ({{ cob.motocicletaPlaca }})</div>
                    </td>
                    <td>#{{ cob.numeroParcela }} ({{ cob.tipoCobranca }})</td>
                    <td>R$ {{ cob.valorTotal | number:'1.2-2' }}</td>
                    <td class="text-success font-bold">R$ {{ cob.valorPago | number:'1.2-2' }}</td>
                    <td class="text-warning font-bold">R$ {{ cob.saldoRestante | number:'1.2-2' }}</td>
                    <td>
                      <span class="badge" [ngClass]="getStatusBadgeClass(cob.status)">
                        {{ cob.status }}
                      </span>
                    </td>
                    <td>
                      <div class="action-buttons">
                        @if (cob.status === 'PENDENTE' || cob.status === 'PAGO_PARCIAL' || cob.status === 'ATRASADO') {
                          <button (click)="openPaymentModal(cob)" class="btn btn-primary btn-sm">
                            💵 Receber
                          </button>
                          <button (click)="openExemptModal(cob)" class="btn btn-outline btn-sm">
                            🔧 Isentar
                          </button>
                        } @else if (cob.status === 'QUITADO') {
                          <button (click)="openPaymentHistoryModal(cob)" class="btn btn-outline btn-sm">
                            👁️ Pagamentos
                          </button>
                        } @else if (cob.status === 'ISENTO') {
                          <span class="text-xs text-dim" title="{{ cob.motivoIsencao }}">Oficina: {{ cob.motivoIsencao }}</span>
                        }
                      </div>
                    </td>
                  </tr>
                }
              }
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal de Recebimento de Pagamento -->
      @if (selectedCobrancaForPay) {
        <div class="modal-backdrop" (click)="selectedCobrancaForPay = null">
          <div class="modal-dialog" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3 class="card-title">Registrar Pagamento: Parcela #{{ selectedCobrancaForPay.numeroParcela }}</h3>
              <button (click)="selectedCobrancaForPay = null" class="btn-close">✕</button>
            </div>
            <form [formGroup]="paymentForm" (ngSubmit)="confirmPayment()">
              <div class="modal-body">
                @if (payError) {
                  <div class="alert alert-danger">{{ payError }}</div>
                }

                <div class="card mb-4" style="background: #0f172a;">
                  <div class="flex justify-between">
                    <div>
                      <small class="text-dim">Cliente:</small>
                      <div><strong>{{ selectedCobrancaForPay.clienteNome }}</strong></div>
                    </div>
                    <div>
                      <small class="text-dim">Saldo Restante:</small>
                      <div class="text-warning font-bold">R$ {{ selectedCobrancaForPay.saldoRestante | number:'1.2-2' }}</div>
                    </div>
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group flex-1">
                    <label class="form-label">Valor a Receber (R$) *</label>
                    <input type="number" formControlName="valor" class="form-control" />
                    <small class="text-dim">Permite pagamento parcial ou quitação integral</small>
                  </div>
                  <div class="form-group flex-1">
                    <label class="form-label">Forma de Pagamento *</label>
                    <select formControlName="formaPagamento" class="form-control">
                      <option value="PIX">Pix (Imediato)</option>
                      <option value="DINHEIRO">Dinheiro em Espécie</option>
                      <option value="TRANSFERENCIA">Transferência Bancária</option>
                      <option value="OUTRO">Outro Meio</option>
                    </select>
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">Código / Referência Pix (Opcional)</label>
                  <input type="text" formControlName="referenciaExterna" class="form-control" placeholder="ID da transação Pix ou comprovante" />
                </div>

                <div class="form-group">
                  <label class="form-label">Observações</label>
                  <input type="text" formControlName="observacao" class="form-control" placeholder="Anotações internas..." />
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" (click)="selectedCobrancaForPay = null" class="btn btn-secondary">Cancelar</button>
                <button type="submit" [disabled]="paymentForm.invalid || savingPay" class="btn btn-primary">
                  {{ savingPay ? 'Confirmando...' : 'Confirmar e Quitar Parcela' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      <!-- Modal de Isenção por Oficina -->
      @if (selectedCobrancaForExempt) {
        <div class="modal-backdrop" (click)="selectedCobrancaForExempt = null">
          <div class="modal-dialog" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3 class="card-title">Conceder Isenção por Manutenção</h3>
              <button (click)="selectedCobrancaForExempt = null" class="btn-close">✕</button>
            </div>
            <div class="modal-body">
              <div class="alert alert-info">
                <strong>Regra Contratual de Uarini:</strong> Dias retidos em oficina sob responsabilidade do locador têm isenção de diária e prorrogam o prazo, não contando como diária quitada para a aquisição Rent-to-Own.
              </div>

              <div class="form-group">
                <label class="form-label">Motivo da Isenção (Obrigatório) *</label>
                <textarea
                  [(ngModel)]="motivoIsencao"
                  class="form-control"
                  rows="3"
                  placeholder="Ex: Motocicleta retida na oficina credenciada para substituição de retentor e troca de óleo do motor."
                ></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button (click)="selectedCobrancaForExempt = null" class="btn btn-secondary">Cancelar</button>
              <button (click)="confirmExemption()" [disabled]="!motivoIsencao.trim() || savingExempt" class="btn btn-primary">
                {{ savingExempt ? 'Registrando...' : 'Confirmar Isenção' }}
              </button>
            </div>
          </div>
        </div>
      }

      <!-- Modal de Histórico de Pagamentos e Estorno Auditado -->
      @if (selectedCobrancaHistory) {
        <div class="modal-backdrop" (click)="selectedCobrancaHistory = null">
          <div class="modal-dialog modal-lg" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3 class="card-title">Pagamentos da Cobrança #{{ selectedCobrancaHistory.numeroParcela }}</h3>
              <button (click)="selectedCobrancaHistory = null" class="btn-close">✕</button>
            </div>
            <div class="modal-body">
              <div class="table-responsive">
                <table class="table">
                  <thead>
                    <tr>
                      <th>Data/Hora</th>
                      <th>Valor</th>
                      <th>Meio</th>
                      <th>Confirmado Por</th>
                      <th>Situação</th>
                      <th>Ação</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (p of selectedCobrancaHistory.pagamentos; track p.id) {
                      <tr>
                        <td>{{ p.dataPagamento }}</td>
                        <td><strong>R$ {{ p.valor | number:'1.2-2' }}</strong></td>
                        <td>{{ p.formaPagamento }}</td>
                        <td>{{ p.confirmadoPor }}</td>
                        <td>
                          @if (p.estornado) {
                            <span class="badge badge-danger">ESTORNADO</span>
                            <div class="text-dim text-xs">{{ p.motivoEstorno }}</div>
                          } @else {
                            <span class="badge badge-success">VÁLIDO</span>
                          }
                        </td>
                        <td>
                          @if (!p.estornado) {
                            <button (click)="reversePayment(p)" class="btn btn-danger btn-sm">
                              Estornar
                            </button>
                          }
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            </div>
            <div class="modal-footer">
              <button (click)="selectedCobrancaHistory = null" class="btn btn-secondary">Fechar</button>
            </div>
          </div>
        </div>
      }

      <!-- Modal de Movimentação de Caução -->
      @if (showCaucaoModal) {
        <div class="modal-backdrop" (click)="showCaucaoModal = false">
          <div class="modal-dialog" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3 class="card-title">Movimentar Caução (Razão Auxiliar)</h3>
              <button (click)="showCaucaoModal = false" class="btn-close">✕</button>
            </div>
            <form [formGroup]="caucaoForm" (ngSubmit)="saveCaucao()">
              <div class="modal-body">
                @if (caucaoError) {
                  <div class="alert alert-danger">{{ caucaoError }}</div>
                }

                <div class="form-group">
                  <label class="form-label">ID do Contrato *</label>
                  <input type="number" formControlName="contratoId" class="form-control" placeholder="ID numérico do contrato" />
                </div>

                <div class="form-row">
                  <div class="form-group flex-1">
                    <label class="form-label">Tipo de Movimento *</label>
                    <select formControlName="tipoMovimento" class="form-control">
                      <option value="ENTRADA">📥 ENTRADA (Depósito de garantia)</option>
                      <option value="RETENCAO_DEBITO">⚠️ RETENÇÃO POR DÉBITO (Avaria/Dívida)</option>
                      <option value="DEVOLUCAO">📤 DEVOLUÇÃO (Final de contrato)</option>
                    </select>
                  </div>
                  <div class="form-group flex-1">
                    <label class="form-label">Valor (R$) *</label>
                    <input type="number" formControlName="valor" class="form-control" placeholder="400.00" />
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">Descrição / Justificativa</label>
                  <input type="text" formControlName="descricao" class="form-control" placeholder="Ex: Depósito inicial de caução via Pix" />
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" (click)="showCaucaoModal = false" class="btn btn-secondary">Cancelar</button>
                <button type="submit" [disabled]="caucaoForm.invalid || savingCaucao" class="btn btn-primary">
                  {{ savingCaucao ? 'Gravando...' : 'Salvar Movimento de Caução' }}
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
    .filter-grid {
      display: flex;
      gap: 12px;
      align-items: flex-end;
      flex-wrap: wrap;
    }
    .filter-actions {
      display: flex;
      gap: 8px;
    }
    .mb-0 {
      margin-bottom: 0;
    }
    .mb-4 {
      margin-bottom: 16px;
    }
    .mb-6 {
      margin-bottom: 24px;
    }
    .text-dim {
      color: #64748b;
    }
    .text-xs {
      font-size: 0.75rem;
    }
    .text-success {
      color: #10b981;
    }
    .text-warning {
      color: #f59e0b;
    }
    .font-bold {
      font-weight: 700;
    }
    .action-buttons {
      display: flex;
      gap: 6px;
      flex-wrap: wrap;
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
    .flex {
      display: flex;
    }
    .justify-between {
      justify-content: space-between;
    }
    .text-center {
      text-align: center;
    }
    .py-6 {
      padding-top: 24px;
      padding-bottom: 24px;
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
export class CobrancasComponent implements OnInit {
  private billingService = inject(BillingService);
  private fb = inject(FormBuilder);

  cobrancas: Cobranca[] = [];
  loading = false;
  selectedStatus = '';
  dataInicio = '';
  dataFim = '';

  selectedCobrancaForPay: Cobranca | null = null;
  savingPay = false;
  payError = '';

  selectedCobrancaForExempt: Cobranca | null = null;
  motivoIsencao = '';
  savingExempt = false;

  selectedCobrancaHistory: Cobranca | null = null;

  showCaucaoModal = false;
  savingCaucao = false;
  caucaoError = '';

  paymentForm = this.fb.group({
    valor: [0, [Validators.required, Validators.min(0.01)]],
    formaPagamento: ['PIX', [Validators.required]],
    referenciaExterna: [''],
    observacao: ['']
  });

  caucaoForm = this.fb.group({
    contratoId: ['', [Validators.required]],
    tipoMovimento: ['ENTRADA', [Validators.required]],
    valor: [400, [Validators.required, Validators.min(0.01)]],
    descricao: ['']
  });

  ngOnInit(): void {
    this.loadCobrancas();
  }

  loadCobrancas(): void {
    this.loading = true;
    this.billingService.list(
      undefined,
      this.selectedStatus || undefined,
      this.dataInicio || undefined,
      this.dataFim || undefined
    ).subscribe({
      next: (res) => {
        this.cobrancas = res.content;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  filterByStatus(status: string): void {
    this.selectedStatus = status;
    this.loadCobrancas();
  }

  clearFilters(): void {
    this.selectedStatus = '';
    this.dataInicio = '';
    this.dataFim = '';
    this.loadCobrancas();
  }

  openPaymentModal(cob: Cobranca): void {
    this.selectedCobrancaForPay = cob;
    this.payError = '';
    this.paymentForm.reset({
      valor: cob.saldoRestante,
      formaPagamento: 'PIX',
      referenciaExterna: '',
      observacao: ''
    });
  }

  confirmPayment(): void {
    if (this.paymentForm.invalid || !this.selectedCobrancaForPay) return;

    this.savingPay = true;
    this.payError = '';

    const payload = {
      cobrancaId: this.selectedCobrancaForPay.id,
      valor: Number(this.paymentForm.value.valor),
      formaPagamento: this.paymentForm.value.formaPagamento!,
      referenciaExterna: this.paymentForm.value.referenciaExterna || undefined,
      observacao: this.paymentForm.value.observacao || undefined,
      idempotencyKey: 'PAY-' + Date.now() + '-' + Math.random().toString(36).substring(2, 7)
    };

    this.billingService.registerPayment(payload).subscribe({
      next: () => {
        this.savingPay = false;
        this.selectedCobrancaForPay = null;
        this.loadCobrancas();
      },
      error: (err) => {
        this.savingPay = false;
        this.payError = err.error?.message || 'Erro ao processar pagamento.';
      }
    });
  }

  openExemptModal(cob: Cobranca): void {
    this.selectedCobrancaForExempt = cob;
    this.motivoIsencao = '';
  }

  confirmExemption(): void {
    if (!this.selectedCobrancaForExempt || !this.motivoIsencao.trim()) return;

    this.savingExempt = true;
    this.billingService.exemptBilling(this.selectedCobrancaForExempt.id, this.motivoIsencao).subscribe({
      next: () => {
        this.savingExempt = false;
        this.selectedCobrancaForExempt = null;
        this.loadCobrancas();
      },
      error: () => {
        this.savingExempt = false;
      }
    });
  }

  openPaymentHistoryModal(cob: Cobranca): void {
    this.selectedCobrancaHistory = cob;
  }

  reversePayment(p: Pagamento): void {
    const motivo = prompt('Informe o motivo do estorno auditado:');
    if (!motivo || !motivo.trim()) return;

    this.billingService.reversePayment(p.id, motivo).subscribe({
      next: () => {
        this.selectedCobrancaHistory = null;
        this.loadCobrancas();
      }
    });
  }

  openCaucaoModal(): void {
    this.caucaoError = '';
    this.caucaoForm.reset({
      tipoMovimento: 'ENTRADA',
      valor: 400
    });
    this.showCaucaoModal = true;
  }

  saveCaucao(): void {
    if (this.caucaoForm.invalid) return;

    this.savingCaucao = true;
    this.caucaoError = '';

    const payload = {
      contratoId: Number(this.caucaoForm.value.contratoId),
      tipoMovimento: this.caucaoForm.value.tipoMovimento!,
      valor: Number(this.caucaoForm.value.valor),
      descricao: this.caucaoForm.value.descricao || undefined
    };

    this.billingService.registerCaucao(payload).subscribe({
      next: () => {
        this.savingCaucao = false;
        this.showCaucaoModal = false;
      },
      error: (err) => {
        this.savingCaucao = false;
        this.caucaoError = err.error?.message || 'Erro ao registrar caução.';
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'QUITADO': return 'badge-success';
      case 'PAGO_PARCIAL': return 'badge-warning';
      case 'ISENTO': return 'badge-info';
      case 'ATRASADO': return 'badge-danger';
      default: return 'badge-muted';
    }
  }
}

