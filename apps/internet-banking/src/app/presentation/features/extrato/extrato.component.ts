import { Component, OnInit, inject } from '@angular/core';
import { AsyncPipe, CurrencyPipe } from '@angular/common';
import { Store } from '@ngrx/store';
import { take } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerIntl } from '@angular/material/datepicker';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { ExtratoActions } from '../../../store/extrato/extrato.actions';
import {
  selectTransacoes, selectCarregando, selectErro,
  selectPaginacao, selectFiltros, selectResumo,
} from '../../../store/extrato/extrato.selectors';
import { FiltroExtrato, filtroExtratoInicial } from '../../../domain/models/extrato.model';
import { ExtratoFiltrosComponent } from './extrato-filtros/extrato-filtros.component';
import { ExtratoTabelaComponent } from './extrato-tabela/extrato-tabela.component';
import { LoadingSpinnerComponent } from '../../components/loading-spinner/loading-spinner.component';
import { ErrorStateComponent } from '../../components/error-state/error-state.component';
import { criarDatepickerIntl, criarPaginatorIntl } from '../../../core/i18n/material-intl';

@Component({
  selector: 'app-extrato',
  standalone: true,
  imports: [
    AsyncPipe, CurrencyPipe, MatIconModule,
    ExtratoFiltrosComponent, ExtratoTabelaComponent,
    LoadingSpinnerComponent, ErrorStateComponent,
  ],
  providers: [
    { provide: MatPaginatorIntl, useFactory: criarPaginatorIntl },
    { provide: MatDatepickerIntl, useFactory: criarDatepickerIntl },
  ],
  template: `
    <div class="p-6 max-w-7xl mx-auto">

            <div class="mb-6">
        <h1 class="text-2xl font-bold text-slate-900">Extrato de Conta</h1>
        <p class="text-slate-500 text-sm mt-1">Acompanhe todas as suas movimentações financeiras</p>
      </div>

            @if (resumo$ | async; as resumo) {
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6" aria-label="Resumo financeiro">

          <div class="bg-white rounded-xl border border-slate-200 p-5 shadow-card"
               role="figure" aria-label="Total de créditos">
            <div class="flex items-center justify-between mb-3">
              <p class="text-sm font-medium text-slate-500">Total Créditos</p>
              <div class="w-8 h-8 rounded-full bg-emerald-50 flex items-center justify-center">
                <mat-icon aria-hidden="true" class="text-emerald-500 !text-base">trending_up</mat-icon>
              </div>
            </div>
            <p class="text-2xl font-bold text-emerald-600">
              {{ resumo.totalCredito | currency:'BRL':'symbol':'1.2-2' }}
            </p>
          </div>

          <div class="bg-white rounded-xl border border-slate-200 p-5 shadow-card"
               role="figure" aria-label="Total de débitos">
            <div class="flex items-center justify-between mb-3">
              <p class="text-sm font-medium text-slate-500">Total Débitos</p>
              <div class="w-8 h-8 rounded-full bg-red-50 flex items-center justify-center">
                <mat-icon aria-hidden="true" class="text-red-500 !text-base">trending_down</mat-icon>
              </div>
            </div>
            <p class="text-2xl font-bold text-red-500">
              {{ resumo.totalDebito | currency:'BRL':'symbol':'1.2-2' }}
            </p>
          </div>

          <div class="bg-white rounded-xl border border-slate-200 p-5 shadow-card"
               role="figure" aria-label="Saldo atual">
            <div class="flex items-center justify-between mb-3">
              <p class="text-sm font-medium text-slate-500">Saldo Atual</p>
              <div class="w-8 h-8 rounded-full bg-blue-50 flex items-center justify-center">
                <mat-icon aria-hidden="true" class="text-blue-600 !text-base">account_balance_wallet</mat-icon>
              </div>
            </div>
            <p [class]="resumo.saldoAtual >= 0
                ? 'text-2xl font-bold text-blue-700'
                : 'text-2xl font-bold text-red-600'">
              {{ resumo.saldoAtual | currency:'BRL':'symbol':'1.2-2' }}
            </p>
          </div>
        </div>
      }

            <app-extrato-filtros
        [filtros]="filtros$ | async"
        (filtrosChange)="onFiltrosChange($event)"
      />

            @if (erro$ | async; as erro) {
        <app-error-state
          [mensagem]="erro"
          (retry)="recarregar()"
        />
      } @else {
        @if (carregando$ | async) {
          <div class="mb-3 rounded-lg border border-blue-100 bg-blue-50 px-4 py-3 text-sm font-medium text-blue-700">
            Atualizando extrato...
          </div>
        }

                <app-extrato-tabela
          [transacoes]="(transacoes$ | async) ?? []"
          [paginacao]="paginacao$ | async"
          (paginaChange)="onPaginaChange($event)"
        />
      }
    </div>
  `,
})
export class ExtratoComponent implements OnInit {
  private store = inject(Store);

  protected transacoes$ = this.store.select(selectTransacoes);
  protected carregando$  = this.store.select(selectCarregando);
  protected erro$        = this.store.select(selectErro);
  protected paginacao$   = this.store.select(selectPaginacao);
  protected filtros$     = this.store.select(selectFiltros);
  protected resumo$      = this.store.select(selectResumo);

  ngOnInit(): void {
    this.store.dispatch(ExtratoActions.carregarExtrato({ filtro: filtroExtratoInicial }));
  }

  protected onFiltrosChange(filtro: FiltroExtrato): void {
    this.store.dispatch(ExtratoActions.carregarExtrato({ filtro }));
  }

  protected onPaginaChange(event: { pagina: number; tamanho: number }): void {
    this.store.select(selectFiltros).pipe(take(1)).subscribe(filtroAtual => {
      this.store.dispatch(ExtratoActions.carregarExtrato({
        filtro: { ...filtroAtual, pagina: event.pagina, tamanhoPagina: event.tamanho },
      }));
    });
  }

  protected recarregar(): void {
    this.store.select(selectFiltros).pipe(take(1)).subscribe(filtro => {
      this.store.dispatch(ExtratoActions.carregarExtrato({ filtro }));
    });
  }
}
