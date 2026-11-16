import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MotorcycleService } from '../../core/services/motorcycle.service';
import { Motocicleta } from '../../core/models/motocicleta.model';

@Component({
  selector: 'app-motos',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './motos.component.html',
  styleUrl: './motos.component.css'
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
