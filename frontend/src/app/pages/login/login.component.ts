import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="login-wrapper">
      <!-- Aurora Horizon Glow inspirada no mockup -->
      <div class="login-aurora"></div>

      <div class="login-card">
        <div class="login-header">
          <div class="brand-pill">
            <span class="pill-dot"></span>
            <span>Plataforma Inteligente de Frotas</span>
          </div>
          <div class="logo-glow">
            <span class="logo-icon">🏍️</span>
          </div>
          <h2>JM MOTOS</h2>
          <p>Gestão Operacional de Frotas & Rent-to-Own</p>
        </div>

        @if (errorMessage) {
          <div class="alert alert-danger">
            {{ errorMessage }}
          </div>
        }

        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label class="form-label">E-mail Operacional</label>
            <input
              type="email"
              formControlName="email"
              class="form-control"
              placeholder="admin@jmmotos.com.br"
            />
            @if (form.get('email')?.touched && form.get('email')?.invalid) {
              <small class="text-danger">E-mail corporativo válido é obrigatório</small>
            }
          </div>

          <div class="form-group">
            <label class="form-label">Senha de Acesso</label>
            <input
              type="password"
              formControlName="senha"
              class="form-control"
              placeholder="••••••••"
            />
            @if (form.get('senha')?.touched && form.get('senha')?.invalid) {
              <small class="text-danger">A senha é obrigatória</small>
            }
          </div>

          <button
            type="submit"
            class="btn btn-primary btn-block"
            [disabled]="form.invalid || loading"
          >
            {{ loading ? 'Autenticando...' : 'Acessar Plataforma' }}
          </button>
        </form>

        <div class="quick-access">
          <p class="quick-title">Credenciais Rápidas de Acesso:</p>
          <div class="quick-buttons">
            <button (click)="fillCredentials('admin@jmmotos.com.br', 'admin123')" class="btn btn-outline btn-sm">
              👑 Administrador
            </button>
            <button (click)="fillCredentials('operador@jmmotos.com.br', 'admin123')" class="btn btn-outline btn-sm">
              🛠️ Operador
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-wrapper {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #05080a;
      background-image: 
        radial-gradient(circle at 50% 0%, rgba(0, 245, 160, 0.2) 0%, rgba(6, 78, 59, 0.1) 40%, transparent 70%),
        radial-gradient(rgba(0, 245, 160, 0.04) 1px, transparent 1px);
      background-size: 100% 100%, 28px 28px;
      padding: 24px;
      position: relative;
      overflow: hidden;
    }

    .login-aurora {
      position: absolute;
      top: -200px;
      left: 50%;
      transform: translateX(-50%);
      width: 600px;
      height: 400px;
      background: radial-gradient(circle, rgba(0, 245, 160, 0.25) 0%, rgba(13, 148, 136, 0.15) 45%, transparent 70%);
      filter: blur(40px);
      pointer-events: none;
    }

    .login-card {
      background: rgba(12, 20, 23, 0.85);
      backdrop-filter: blur(24px);
      -webkit-backdrop-filter: blur(24px);
      border: 1px solid rgba(0, 245, 160, 0.2);
      border-radius: 20px;
      padding: 40px 36px;
      width: 100%;
      max-width: 440px;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.85), 0 0 30px rgba(0, 245, 160, 0.15);
      position: relative;
      z-index: 10;
    }

    .login-card::before {
      content: '';
      position: absolute;
      top: 0;
      left: 20%;
      right: 20%;
      height: 2px;
      background: linear-gradient(90deg, transparent, #00f5a0, transparent);
      box-shadow: 0 0 10px #00f5a0;
    }

    .login-header {
      text-align: center;
      margin-bottom: 28px;
    }

    .brand-pill {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 4px 14px;
      border-radius: 9999px;
      background: rgba(0, 245, 160, 0.08);
      border: 1px solid rgba(0, 245, 160, 0.25);
      color: #00f5a0;
      font-size: 0.72rem;
      font-weight: 700;
      letter-spacing: 0.05em;
      text-transform: uppercase;
      margin-bottom: 18px;
    }

    .pill-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background-color: #00f5a0;
      box-shadow: 0 0 8px #00f5a0;
    }

    .logo-glow {
      width: 58px;
      height: 58px;
      border-radius: 16px;
      background: linear-gradient(135deg, rgba(0, 245, 160, 0.2), rgba(6, 78, 59, 0.4));
      border: 1px solid rgba(0, 245, 160, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 14px;
      box-shadow: 0 0 25px rgba(0, 245, 160, 0.35);
    }

    .logo-icon {
      font-size: 28px;
    }

    .login-header h2 {
      font-size: 1.8rem;
      font-weight: 800;
      color: #ffffff;
      letter-spacing: 0.04em;
      text-shadow: 0 0 15px rgba(0, 245, 160, 0.3);
    }

    .login-header p {
      font-size: 0.85rem;
      color: var(--text-muted);
      margin-top: 4px;
    }

    .btn-block {
      width: 100%;
      padding: 13px;
      font-size: 0.95rem;
      margin-top: 10px;
    }

    .text-danger {
      color: #f87171;
      font-size: 0.75rem;
      margin-top: 4px;
    }

    .quick-access {
      margin-top: 28px;
      padding-top: 22px;
      border-top: 1px solid rgba(0, 245, 160, 0.12);
      text-align: center;
    }

    .quick-title {
      font-size: 0.75rem;
      color: var(--text-muted);
      margin-bottom: 12px;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    .quick-buttons {
      display: flex;
      gap: 10px;
      justify-content: center;
    }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    email: ['admin@jmmotos.com.br', [Validators.required, Validators.email]],
    senha: ['admin123', [Validators.required]]
  });

  loading = false;
  errorMessage = '';

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading = true;
    this.errorMessage = '';

    const { email, senha } = this.form.value;

    this.authService.login(email!, senha!).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'E-mail ou senha incorretos.';
      }
    });
  }

  fillCredentials(email: string, pass: string): void {
    this.form.patchValue({ email, senha: pass });
  }
}
