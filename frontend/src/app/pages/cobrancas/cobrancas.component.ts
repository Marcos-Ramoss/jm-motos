import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { BillingService } from '../../core/services/billing.service';
import { Cobranca } from '../../core/models/cobranca.model';
import { CaucaoMovimento } from '../../core/models/caucao-movimento.model';
import { Pagamento } from '../../core/models/pagamento.model';

@Component({
  selector: 'app-cobrancas',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './cobrancas.component.html',
  styleUrl: './cobrancas.component.css'
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
