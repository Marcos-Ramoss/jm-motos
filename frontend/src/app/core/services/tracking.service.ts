import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TrackingStatus } from '../models/tracking-status.model';

@Injectable({
  providedIn: 'root'
})
export class TrackingService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/rastreamento/status';

  constructor(private http: HttpClient) {}

  getStatus(): Observable<TrackingStatus> {
    return this.http.get<TrackingStatus>(this.apiUrl);
  }
}

