import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CustomerService } from '../../core/services/customer.service';
import { Cliente } from '../../core/models/cliente.model';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './clientes.component.html',
  styleUrl: './clientes.component.css'
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
