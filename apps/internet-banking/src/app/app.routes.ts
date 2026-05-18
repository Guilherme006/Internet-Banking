import { Routes } from '@angular/router';

export const APP_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'extrato',
    pathMatch: 'full',
  },
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
  {
    path: '**',
    redirectTo: 'extrato',
  },
];
