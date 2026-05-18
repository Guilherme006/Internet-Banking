import { Routes } from '@angular/router';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { provideEffects } from '@ngrx/effects';
import { provideState } from '@ngrx/store';

import { ExtratoEffects } from '../../../store/extrato/extrato.effects';
import { extratoReducer } from '../../../store/extrato/extrato.reducer';
import { PagamentoEffects } from '../../../store/pagamento/pagamento.effects';
import { pagamentoReducer } from '../../../store/pagamento/pagamento.reducer';

export const BANKING_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./layout.component').then(m => m.LayoutComponent),
    providers: [
      provideNativeDateAdapter(),
      { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' },
      provideState('extrato', extratoReducer),
      provideState('pagamento', pagamentoReducer),
      provideEffects([ExtratoEffects, PagamentoEffects]),
    ],
    children: [
      {
        path: 'extrato',
        loadComponent: () =>
          import('../extrato/extrato.component').then(
            m => m.ExtratoComponent
          ),
        title: 'Extrato | Internet Banking',
      },
      {
        path: 'pagamento',
        loadComponent: () =>
          import('../pagamento/pagamento.component').then(
            m => m.PagamentoComponent
          ),
        title: 'Pagar Boleto | Internet Banking',
      },
      {
        path: 'minha-conta',
        loadComponent: () =>
          import('../minha-conta/minha-conta.component').then(
            m => m.MinhaContaComponent
          ),
        title: 'Minha Conta | Internet Banking',
      },
    ],
  },
];
