import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Motocicleta } from '../models/motocicleta.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class MotorcycleService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/motos';

  constructor(private http: HttpClient) {}

  list(busca?: string, status?: string, page: number = 0, size: number = 15): Observable<Page<Motocicleta>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (busca) params = params.set('busca', busca);
    if (status) params = params.set('status', status);

    return this.http.get<Page<Motocicleta>>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Motocicleta> {
    return this.http.get<Motocicleta>(`${this.apiUrl}/${id}`);
  }

  create(moto: Partial<Motocicleta>): Observable<Motocicleta> {
    return this.http.post<Motocicleta>(this.apiUrl, moto);
  }

  update(id: number, moto: Partial<Motocicleta>): Observable<Motocicleta> {
    return this.http.put<Motocicleta>(`${this.apiUrl}/${id}`, moto);
  }

  updateStatus(id: number, statusOperacional: string): Observable<Motocicleta> {
    return this.http.patch<Motocicleta>(`${this.apiUrl}/${id}/status`, { statusOperacional });
  }
}

