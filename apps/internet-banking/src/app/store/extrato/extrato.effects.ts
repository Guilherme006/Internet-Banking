import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';
import { ExtratoActions } from './extrato.actions';
import { ExtratoRepository } from '../../data/repositories/extrato.repository';
import { Paginacao } from '../../domain/models/extrato.model';

@Injectable()
export class ExtratoEffects {
  private actions$ = inject(Actions);
  private extratoRepository = inject(ExtratoRepository);

  carregarExtrato$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ExtratoActions.carregarExtrato),
      switchMap(({ filtro }) =>
        this.extratoRepository.buscar(filtro).pipe(
          map(response => {
            const paginacao: Paginacao = {
              paginaAtual: response.number,
              totalElementos: response.totalElements,
              tamanhoPagina: response.size,
              totalPaginas: response.totalPages,
            };
            return ExtratoActions.carregarExtratoSucesso({
              transacoes: response.content,
              paginacao,
            });
          }),
          catchError(err => {
            const mensagem = err?.error?.detail ?? 'Falha ao carregar o extrato.';
            return of(ExtratoActions.carregarExtratoFalha({ erro: mensagem }));
          })
        )
      )
    )
  );
}
