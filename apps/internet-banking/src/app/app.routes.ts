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
    loadChildren: () =>
      import('./presentation/features/layout/banking.routes').then(
        m => m.BANKING_ROUTES
      ),
  },
  {
    path: '**',
    redirectTo: 'extrato',
  },
];
