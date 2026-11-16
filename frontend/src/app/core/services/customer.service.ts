import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../models/cliente.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/clientes';

  constructor(private http: HttpClient) {}

  list(busca?: string, status?: string, page: number = 0, size: number = 15): Observable<Page<Cliente>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (busca) params = params.set('busca', busca);
    if (status) params = params.set('status', status);

    return this.http.get<Page<Cliente>>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`);
  }

  create(cliente: Partial<Cliente>): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente);
  }

  update(id: number, cliente: Partial<Cliente>): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${id}`, cliente);
  }
}

