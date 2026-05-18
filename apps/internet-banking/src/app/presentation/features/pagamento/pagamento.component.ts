import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { AsyncPipe, SlicePipe } from '@angular/common';
import { Store } from '@ngrx/store';
import { MatIconModule } from '@angular/material/icon';
import { PagamentoActions } from '../../../store/pagamento/pagamento.actions';
import {
  selectBoleto, selectStatus, selectComprovante,
  selectErroPagamento, selectEstaProcessando, selectPagamentoAprovado,
} from '../../../store/pagamento/pagamento.selectors';
import { PagamentoRequest } from '../../../domain/models/pagamento.model';
import { IdempotencyService } from '../../../core/services/idempotency.service';
import { AuthService } from '../../../core/services/auth.service';
import { CircuitBreakerService } from '../../../core/services/circuit-breaker.service';
import { BoletoFormComponent } from './boleto-form/boleto-form.component';
import { ComprovanteComponent } from './comprovante/comprovante.component';
import { ErrorStateComponent } from '../../components/error-state/error-state.component';

@Component({
  selector: 'app-pagamento',
  standalone: true,
  imports: [AsyncPipe, SlicePipe, MatIconModule, BoletoFormComponent, ComprovanteComponent, ErrorStateComponent],
  template: `
    <div class="p-6 max-w-2xl mx-auto">

            <div class="mb-6">
        <h1 class="text-2xl font-bold text-slate-900">Pagar Boleto</h1>
        <p class="text-slate-500 text-sm mt-1">Digite ou cole o código de barras para pagar seu boleto</p>
      </div>

            @if (circuitBreaker.circuitoAberto()) {
        <div
          role="alert"
          aria-live="assertive"
          class="flex items-start gap-3 bg-amber-50 border border-amber-200 rounded-xl p-4 mb-5"
        >
          <mat-icon aria-hidden="true" class="text-amber-600 mt-0.5">warning_amber</mat-icon>
          <div>
            <p class="text-sm font-semibold text-amber-800">Sistema com instabilidade</p>
            <p class="text-sm text-amber-600 mt-0.5">
              Estamos enfrentando lentidão nos servidores. Seus dados estão seguros.
            </p>
            <button
              class="text-sm text-amber-700 underline mt-2 hover:text-amber-900"
              (click)="tentarNovamente()"
              aria-label="Tentar reconectar ao serviço"
            >
              Tentar novamente
            </button>
          </div>
        </div>
      }

            <div class="bg-white rounded-2xl border border-slate-200 shadow-card p-6">

                <div class="flex items-center gap-2 bg-blue-50 rounded-lg px-3 py-2 mb-5">
          <mat-icon aria-hidden="true" class="text-blue-500 !text-sm">shield</mat-icon>
          <p class="text-xs text-blue-700">
            Sessão protegida contra duplo débito
            <span class="font-mono text-blue-500 ml-1">{{ chaveIdempotencia | slice:0:8 }}...</span>
          </p>
        </div>

                @if (pagamentoAprovado$ | async) {
          @if (comprovante$ | async; as comprovante) {
            <app-comprovante
              [comprovante]="comprovante"
              (novoPagamento)="resetarPagamento()"
            />
          }
        }

                @else if ((status$ | async) === 'ERRO') {
          <app-error-state
            [mensagem]="(erro$ | async) ?? 'Falha ao processar o pagamento.'"
            (retry)="resetarPagamento()"
          />
        }

                @else {
          <app-boleto-form
            [boleto]="boleto$ | async"
            [validacaoAtiva]="(status$ | async) === 'VALIDANDO'"
            [processando]="(estaProcessando$ | async) ?? false"
            [numeroConta]="usuario.conta"
            (buscarBoleto)="onBuscarBoleto($event)"
            (confirmarPagamento)="onConfirmarPagamento($event)"
          />
        }
      </div>
    </div>
  `,
})
export class PagamentoComponent implements OnInit, OnDestroy {
  private store = inject(Store);
  private idempotencyService = inject(IdempotencyService);
  protected circuitBreaker = inject(CircuitBreakerService);
  protected usuario = inject(AuthService).obterUsuario();

  protected boleto$          = this.store.select(selectBoleto);
  protected status$          = this.store.select(selectStatus);
  protected comprovante$     = this.store.select(selectComprovante);
  protected erro$            = this.store.select(selectErroPagamento);
  protected estaProcessando$ = this.store.select(selectEstaProcessando);
  protected pagamentoAprovado$ = this.store.select(selectPagamentoAprovado);
  protected chaveIdempotencia = '';

  ngOnInit(): void {
    this.chaveIdempotencia = this.idempotencyService.resetar();
    this.store.dispatch(PagamentoActions.resetarPagamento());
  }

  ngOnDestroy(): void {
    this.store.dispatch(PagamentoActions.resetarPagamento());
  }

  protected onBuscarBoleto(codigoBarra: string): void {
    this.store.dispatch(PagamentoActions.buscarBoleto({ codigoBarra }));
  }

  protected onConfirmarPagamento(request: PagamentoRequest): void {
    this.store.dispatch(PagamentoActions.confirmarPagamento({ request }));
  }

  protected resetarPagamento(): void {
    this.chaveIdempotencia = this.idempotencyService.resetar();
    this.store.dispatch(PagamentoActions.resetarPagamento());
  }

  protected tentarNovamente(): void {
    this.circuitBreaker.tentarNovamente();
    this.resetarPagamento();
  }
}
