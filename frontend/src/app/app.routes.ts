import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { MainLayoutComponent } from './layout/main-layout.component';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { MotosComponent } from './pages/motos/motos.component';
import { ClientesComponent } from './pages/clientes/clientes.component';
import { ContratosComponent } from './pages/contratos/contratos.component';
import { CobrancasComponent } from './pages/cobrancas/cobrancas.component';
import { RastreamentoComponent } from './pages/rastreamento/rastreamento.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent
      },
      {
        path: 'motos',
        component: MotosComponent
      },
      {
        path: 'clientes',
        component: ClientesComponent
      },
      {
        path: 'contratos',
        component: ContratosComponent
      },
      {
        path: 'cobrancas',
        component: CobrancasComponent
      },
      {
        path: 'rastreamento',
        component: RastreamentoComponent
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
