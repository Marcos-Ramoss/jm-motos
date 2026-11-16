import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardSummary } from '../models/dashboard-summary.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/dashboard/resumo';

  constructor(private http: HttpClient) {}

  getSummary(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(this.apiUrl);
  }
}

