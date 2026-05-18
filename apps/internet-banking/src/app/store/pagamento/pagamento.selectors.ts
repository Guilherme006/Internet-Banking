import { createFeatureSelector, createSelector } from '@ngrx/store';
import { PagamentoState } from './pagamento.reducer';

export const selectPagamentoState = createFeatureSelector<PagamentoState>('pagamento');

export const selectBoleto      = createSelector(selectPagamentoState, s => s.boleto);
export const selectStatus      = createSelector(selectPagamentoState, s => s.status);
export const selectComprovante = createSelector(selectPagamentoState, s => s.comprovante);
export const selectErroPagamento = createSelector(selectPagamentoState, s => s.erro);

export const selectEstaProcessando = createSelector(
  selectStatus,
  status => status === 'PROCESSANDO' || status === 'VALIDANDO'
);

export const selectPagamentoAprovado = createSelector(
  selectStatus,
  status => status === 'APROVADO'
);
