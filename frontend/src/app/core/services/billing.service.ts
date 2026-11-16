import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CaucaoMovimento, Cobranca, Page, Pagamento } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class BillingService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/cobrancas';

  constructor(private http: HttpClient) {}

  list(
    contratoId?: number,
    status?: string,
    dataInicio?: string,
    dataFim?: string,
    page: number = 0,
    size: number = 20
  ): Observable<Page<Cobranca>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (contratoId) params = params.set('contratoId', contratoId.toString());
    if (status) params = params.set('status', status);
    if (dataInicio) params = params.set('dataInicio', dataInicio);
    if (dataFim) params = params.set('dataFim', dataFim);

    return this.http.get<Page<Cobranca>>(this.apiUrl, { params });
  }

  registerPayment(payload: {
    cobrancaId: number;
    valor: number;
    formaPagamento: string;
    referenciaExterna?: string;
    comprovanteUrl?: string;
    observacao?: string;
    idempotencyKey?: string;
  }): Observable<Pagamento> {
    return this.http.post<Pagamento>(`${this.apiUrl}/pagamentos`, payload);
  }

  exemptBilling(id: number, motivoIsencao: string): Observable<Cobranca> {
    return this.http.post<Cobranca>(`${this.apiUrl}/${id}/isentar`, { motivoIsencao });
  }

  reversePayment(id: number, motivoEstorno: string): Observable<Pagamento> {
    return this.http.post<Pagamento>(`${this.apiUrl}/pagamentos/${id}/estornar`, { motivoEstorno });
  }

  registerCaucao(payload: {
    contratoId: number;
    tipoMovimento: string;
    valor: number;
    descricao?: string;
    comprovanteUrl?: string;
  }): Observable<CaucaoMovimento> {
    return this.http.post<CaucaoMovimento>(`${this.apiUrl}/caucao`, payload);
  }

  listCaucao(contratoId?: number): Observable<CaucaoMovimento[]> {
    let params = new HttpParams();
    if (contratoId) params = params.set('contratoId', contratoId.toString());
    return this.http.get<CaucaoMovimento[]>(`${this.apiUrl}/caucao`, { params });
  }
}

