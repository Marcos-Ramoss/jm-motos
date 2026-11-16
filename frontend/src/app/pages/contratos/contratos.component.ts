import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ContractService } from '../../core/services/contract.service';
import { MotorcycleService } from '../../core/services/motorcycle.service';
import { CustomerService } from '../../core/services/customer.service';
import { Contrato } from '../../core/models/contrato.model';
import { Cliente } from '../../core/models/cliente.model';
import { Motocicleta } from '../../core/models/motocicleta.model';
import { Plano } from '../../core/models/plano.model';

@Component({
  selector: 'app-contratos',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './contratos.component.html',
  styleUrl: './contratos.component.css'
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
