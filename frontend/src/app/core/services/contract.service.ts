import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Contrato } from '../models/contrato.model';
import { Page } from '../models/page.model';
import { Plano } from '../models/plano.model';

@Injectable({
  providedIn: 'root'
})
export class ContractService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/contratos';
  private readonly planosUrl = 'http://localhost:8080/api/v1/planos';

  constructor(private http: HttpClient) {}

  list(busca?: string, status?: string, page: number = 0, size: number = 15): Observable<Page<Contrato>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (busca) params = params.set('busca', busca);
    if (status) params = params.set('status', status);

    return this.http.get<Page<Contrato>>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Contrato> {
    return this.http.get<Contrato>(`${this.apiUrl}/${id}`);
  }

  create(payload: {
    clienteId: number;
    motocicletaId: number;
    planoVersaoId: number;
    dataInicio: string;
    diaVencimentoPersonalizado?: number;
    observacoes?: string;
  }): Observable<Contrato> {
    return this.http.post<Contrato>(this.apiUrl, payload);
  }

  cancel(id: number, motivo?: string): Observable<Contrato> {
    const params = motivo ? new HttpParams().set('motivo', motivo) : undefined;
    return this.http.post<Contrato>(`${this.apiUrl}/${id}/cancelar`, {}, { params });
  }

  listPlans(): Observable<Plano[]> {
    return this.http.get<Plano[]>(this.planosUrl);
  }
}

