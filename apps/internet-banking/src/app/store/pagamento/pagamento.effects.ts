import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Router } from '@angular/router';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { PagamentoActions } from './pagamento.actions';
import { PagamentoRepository } from '../../data/repositories/pagamento.repository';
import { IdempotencyService } from '../../core/services/idempotency.service';
import { NotificationService } from '../../core/services/notification.service';

@Injectable()
export class PagamentoEffects {
  private actions$ = inject(Actions);
  private pagamentoRepository = inject(PagamentoRepository);
  private idempotencyService = inject(IdempotencyService);
  private notification = inject(NotificationService);
  private router = inject(Router);

  buscarBoleto$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PagamentoActions.buscarBoleto),
      switchMap(({ codigoBarra }) =>
        this.pagamentoRepository.buscarDadosBoleto(codigoBarra).pipe(
          map(boleto => PagamentoActions.buscarBoletoSucesso({ boleto })),
          catchError(err => {
            const mensagem = err?.error?.detail ?? 'Boleto não encontrado.';
            return of(PagamentoActions.buscarBoletoFalha({ erro: mensagem }));
          })
        )
      )
    )
  );

  confirmarPagamento$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PagamentoActions.confirmarPagamento),
      switchMap(({ request }) =>
        this.pagamentoRepository.pagarBoleto(request).pipe(
          map(comprovante => {
            this.idempotencyService.resetar();
            return PagamentoActions.pagamentoSucesso({ comprovante });
          }),
          catchError(err => {
            const mensagem = err?.error?.detail ?? 'Falha ao processar o pagamento.';
            return of(PagamentoActions.pagamentoFalha({ erro: mensagem }));
          })
        )
      )
    )
  );

  notificarSucesso$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(PagamentoActions.pagamentoSucesso),
        tap(({ comprovante }) => {
          const msg = comprovante.reprocessado
            ? `Pagamento já havia sido processado. Comprovante: ${comprovante.transacaoId}`
            : `Boleto pago com sucesso! Comprovante: ${comprovante.transacaoId}`;
          this.notification.sucesso(msg);
        })
      ),
    { dispatch: false }
  );
}
