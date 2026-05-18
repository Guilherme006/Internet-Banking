import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ExtratoState } from './extrato.reducer';
import { ResumoExtrato } from '../../domain/models/extrato.model';

export const selectExtratoState = createFeatureSelector<ExtratoState>('extrato');

export const selectTransacoes  = createSelector(selectExtratoState, s => s.transacoes);
export const selectCarregando  = createSelector(selectExtratoState, s => s.carregando);
export const selectErro        = createSelector(selectExtratoState, s => s.erro);
export const selectPaginacao   = createSelector(selectExtratoState, s => s.paginacao);
export const selectFiltros     = createSelector(selectExtratoState, s => s.filtros);

export const selectResumo = createSelector(selectTransacoes, (transacoes): ResumoExtrato => {
  const totalCredito = transacoes
    .filter(t => t.tipo === 'CREDITO')
    .reduce((acc, t) => acc + t.valor, 0);

  const totalDebito = transacoes
    .filter(t => t.tipo === 'DEBITO')
    .reduce((acc, t) => acc + t.valor, 0);

  const saldoAtual = transacoes.length > 0
    ? transacoes[0].saldoApos
    : 0;

  return { totalCredito, totalDebito, saldoAtual };
});
