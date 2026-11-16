import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginResponse } from '../models/login-response.model';
import { User } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/auth';
  private readonly TOKEN_KEY = 'jm_motos_token';
  private readonly USER_KEY = 'jm_motos_user';

  currentUser = signal<User | null>(this.getStoredUser());

  constructor(private http: HttpClient) {}

  login(email: string, senha: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { email, senha }).pipe(
      tap(res => {
        localStorage.setItem(this.TOKEN_KEY, res.token);
        localStorage.setItem(this.USER_KEY, JSON.stringify(res.usuario));
        this.currentUser.set(res.usuario);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUser.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  hasRole(role: string): boolean {
    const user = this.currentUser();
    return user ? user.perfil === role : false;
  }

  private getStoredUser(): User | null {
    const data = localStorage.getItem(this.USER_KEY);
    return data ? JSON.parse(data) : null;
  }
}

