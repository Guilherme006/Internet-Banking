import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/guards/auth.guard';

export const APP_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./presentation/features/auth/login.component').then(
        m => m.LoginComponent
      ),
    title: 'Login | Internet Banking',
  },
  {
    path: 'cadastro',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./presentation/features/auth/cadastro.component').then(
        m => m.CadastroComponent
      ),
    title: 'Cadastro | Internet Banking',
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./presentation/features/layout/layout.component').then(
        m => m.LayoutComponent
      ),
    children: [
      {
        path: 'extrato',
        loadComponent: () =>
          import('./presentation/features/extrato/extrato.component').then(
            m => m.ExtratoComponent
          ),
        title: 'Extrato | Internet Banking',
      },
      {
        path: 'pagamento',
        loadComponent: () =>
          import('./presentation/features/pagamento/pagamento.component').then(
            m => m.PagamentoComponent
          ),
        title: 'Pagar Boleto | Internet Banking',
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'extrato',
  },
];
