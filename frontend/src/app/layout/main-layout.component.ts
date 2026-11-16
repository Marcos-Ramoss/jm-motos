import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="app-container">
      <!-- Sidebar Desktop Cyber Emerald -->
      <aside class="sidebar-desktop">
        <div class="brand">
          <div class="brand-logo-glow">
            <span class="brand-icon">🏍️</span>
          </div>
          <div>
            <div class="brand-title">JM MOTOS</div>
            <div class="brand-subtitle">Smart Fleet & Rent-to-Own</div>
          </div>
        </div>

        <nav class="nav-menu">
          <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">📊</span>
            <span>Dashboard</span>
          </a>
          <a routerLink="/motos" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">🛵</span>
            <span>Motocicletas</span>
          </a>
          <a routerLink="/clientes" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">👥</span>
            <span>Clientes</span>
          </a>
          <a routerLink="/contratos" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">📑</span>
            <span>Contratos</span>
          </a>
          <a routerLink="/cobrancas" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">💳</span>
            <span>Cobranças & Caixa</span>
          </a>
          <a routerLink="/rastreamento" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">📍</span>
            <span>Rastreamento GPS</span>
          </a>
        </nav>

        <div class="sidebar-footer">
          <div class="user-card">
            <div class="user-avatar">
              {{ authService.currentUser()?.nome?.charAt(0) || 'U' }}
            </div>
            <div class="user-info">
              <div class="user-name">{{ authService.currentUser()?.nome || 'Operador' }}</div>
              <div class="user-role">{{ authService.currentUser()?.perfil || 'ADMIN' }}</div>
            </div>
          </div>
          <button (click)="logout()" class="btn-logout" title="Sair da aplicação">
            Sair 🚪
          </button>
        </div>
      </aside>

      <!-- Conteúdo Principal com Horizon Glow Aurora -->
      <main class="main-content">
        <!-- Efeito de Aurora / Horizon Glow do Mockup -->
        <div class="horizon-glow"></div>

        <!-- Header Mobile / Topbar -->
        <header class="topbar-mobile">
          <div class="brand-mobile">
            <div class="brand-logo-glow-sm">
              <span class="brand-icon-sm">🏍️</span>
            </div>
            <span class="brand-title">JM MOTOS</span>
          </div>
          <div class="topbar-actions">
            <span class="badge badge-success">{{ authService.currentUser()?.perfil }}</span>
            <button (click)="logout()" class="btn btn-outline btn-sm">Sair</button>
          </div>
        </header>

        <div class="content-body">
          <router-outlet></router-outlet>
        </div>

        <!-- Bottom Navigation Mobile (Mobile-first UX) -->
        <nav class="bottom-nav-mobile">
          <a routerLink="/dashboard" routerLinkActive="active" class="bottom-nav-item">
            <span>📊</span>
            <small>Dashboard</small>
          </a>
          <a routerLink="/motos" routerLinkActive="active" class="bottom-nav-item">
            <span>🛵</span>
            <small>Motos</small>
          </a>
          <a routerLink="/contratos" routerLinkActive="active" class="bottom-nav-item">
            <span>📑</span>
            <small>Contratos</small>
          </a>
          <a routerLink="/cobrancas" routerLinkActive="active" class="bottom-nav-item">
            <span>💳</span>
            <small>Cobranças</small>
          </a>
          <a routerLink="/rastreamento" routerLinkActive="active" class="bottom-nav-item">
            <span>📍</span>
            <small>GPS</small>
          </a>
        </nav>
      </main>
    </div>
  `,
  styles: [`
    .sidebar-desktop {
      width: 275px;
      background: rgba(6, 11, 14, 0.92);
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
      border-right: 1px solid rgba(0, 245, 160, 0.12);
      display: flex;
      flex-direction: column;
      flex-shrink: 0;
      height: 100vh;
      position: sticky;
      top: 0;
      z-index: 50;
    }

    .brand {
      padding: 24px 20px;
      display: flex;
      align-items: center;
      gap: 14px;
      border-bottom: 1px solid rgba(0, 245, 160, 0.1);
      background: linear-gradient(180deg, rgba(0, 245, 160, 0.04) 0%, transparent 100%);
    }

    .brand-logo-glow {
      width: 44px;
      height: 44px;
      border-radius: 12px;
      background: linear-gradient(135deg, rgba(0, 245, 160, 0.25) 0%, rgba(6, 78, 59, 0.4) 100%);
      border: 1px solid rgba(0, 245, 160, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 0 20px rgba(0, 245, 160, 0.3);
      flex-shrink: 0;
    }

    .brand-icon {
      font-size: 22px;
    }

    .brand-title {
      font-size: 1.25rem;
      font-weight: 800;
      letter-spacing: 0.06em;
      color: #ffffff;
      text-shadow: 0 0 15px rgba(0, 245, 160, 0.35);
    }

    .brand-subtitle {
      font-size: 0.7rem;
      color: var(--text-muted);
      font-weight: 600;
      letter-spacing: 0.02em;
    }

    .nav-menu {
      padding: 24px 14px;
      display: flex;
      flex-direction: column;
      gap: 6px;
      flex: 1;
      overflow-y: auto;
    }

    .nav-item {
      display: flex;
      align-items: center;
      gap: 14px;
      padding: 12px 18px;
      border-radius: 10px;
      color: var(--text-muted);
      text-decoration: none;
      font-weight: 600;
      font-size: 0.9rem;
      transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
      border-left: 3px solid transparent;
    }

    .nav-item:hover {
      background-color: rgba(0, 245, 160, 0.06);
      color: #ffffff;
      transform: translateX(3px);
    }

    .nav-item.active {
      background: linear-gradient(90deg, rgba(0, 245, 160, 0.16) 0%, rgba(0, 245, 160, 0.02) 100%);
      color: #00f5a0;
      border-left: 3px solid #00f5a0;
      box-shadow: inset 0 0 15px rgba(0, 245, 160, 0.08);
      font-weight: 700;
    }

    .nav-icon {
      font-size: 18px;
    }

    .sidebar-footer {
      padding: 18px;
      border-top: 1px solid rgba(0, 245, 160, 0.1);
      background-color: rgba(4, 8, 10, 0.95);
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 10px;
    }

    .user-card {
      display: flex;
      align-items: center;
      gap: 10px;
      overflow: hidden;
      flex: 1;
    }

    .user-avatar {
      width: 38px;
      height: 38px;
      background: linear-gradient(135deg, #00f5a0 0%, #0d9488 100%);
      color: #021a12;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 800;
      font-size: 0.95rem;
      box-shadow: 0 0 15px rgba(0, 245, 160, 0.4);
      flex-shrink: 0;
    }

    .user-info {
      overflow: hidden;
    }

    .user-name {
      font-size: 0.85rem;
      font-weight: 700;
      color: #ffffff;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }

    .user-role {
      font-size: 0.7rem;
      color: #00f5a0;
      font-weight: 600;
      letter-spacing: 0.05em;
    }

    .btn-logout {
      background: rgba(248, 113, 113, 0.08);
      border: 1px solid rgba(248, 113, 113, 0.25);
      color: #f87171;
      font-size: 0.8rem;
      font-weight: 600;
      cursor: pointer;
      padding: 6px 10px;
      border-radius: 8px;
      transition: all 0.2s ease;
    }
    .btn-logout:hover {
      background-color: rgba(248, 113, 113, 0.2);
      border-color: #f87171;
      color: #ffffff;
    }

    /* Brilho de Aurora no topo do conteúdo (efeito neon do layout do usuário) */
    .horizon-glow {
      position: absolute;
      top: -150px;
      left: 50%;
      transform: translateX(-50%);
      width: 80%;
      max-width: 900px;
      height: 300px;
      background: radial-gradient(ellipse at center, rgba(0, 245, 160, 0.15) 0%, rgba(6, 78, 59, 0.05) 50%, transparent 70%);
      pointer-events: none;
      z-index: 0;
    }

    .topbar-mobile {
      display: none;
      align-items: center;
      justify-content: space-between;
      padding: 14px 18px;
      background: rgba(6, 11, 14, 0.95);
      backdrop-filter: blur(16px);
      border-bottom: 1px solid rgba(0, 245, 160, 0.12);
      position: sticky;
      top: 0;
      z-index: 40;
    }

    .brand-mobile {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .brand-logo-glow-sm {
      width: 32px;
      height: 32px;
      border-radius: 8px;
      background: linear-gradient(135deg, rgba(0, 245, 160, 0.25), rgba(6, 78, 59, 0.4));
      border: 1px solid rgba(0, 245, 160, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .brand-icon-sm {
      font-size: 16px;
    }

    .topbar-actions {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .bottom-nav-mobile {
      display: none;
    }

    @media (max-width: 768px) {
      .sidebar-desktop {
        display: none;
      }

      .topbar-mobile {
        display: flex;
      }

      .bottom-nav-mobile {
        display: flex;
        position: fixed;
        bottom: 0;
        left: 0;
        right: 0;
        height: 68px;
        background: rgba(6, 11, 14, 0.96);
        backdrop-filter: blur(20px);
        -webkit-backdrop-filter: blur(20px);
        border-top: 1px solid rgba(0, 245, 160, 0.15);
        z-index: 999;
        justify-content: space-around;
        align-items: center;
        padding: 0 6px;
      }

      .bottom-nav-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 3px;
        color: var(--text-muted);
        text-decoration: none;
        font-size: 0.72rem;
        flex: 1;
        height: 100%;
        transition: all 0.2s ease;
      }

      .bottom-nav-item span {
        font-size: 20px;
      }

      .bottom-nav-item.active {
        color: #00f5a0;
        font-weight: 700;
        text-shadow: 0 0 10px rgba(0, 245, 160, 0.4);
      }
    }
  `]
})
export class MainLayoutComponent {
  authService = inject(AuthService);
  private router = inject(Router);

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
