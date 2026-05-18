import { createReducer, on } from '@ngrx/store';
import { Boleto } from '../../domain/models/boleto.model';
import { Comprovante, StatusPagamento } from '../../domain/models/pagamento.model';
import { PagamentoActions } from './pagamento.actions';

export interface PagamentoState {
  readonly boleto: Boleto | null;
  readonly status: StatusPagamento;
  readonly comprovante: Comprovante | null;
  readonly erro: string | null;
}

const estadoInicial: PagamentoState = {
  boleto: null,
  status: 'OCIOSO',
  comprovante: null,
  erro: null,
};

export const pagamentoReducer = createReducer(
  estadoInicial,

  on(PagamentoActions.buscarBoleto, state => ({
    ...state,
    status: 'VALIDANDO' as StatusPagamento,
    boleto: null,
    erro: null,
  })),

  on(PagamentoActions.buscarBoletoSucesso, (state, { boleto }) => ({
    ...state,
    status: 'OCIOSO' as StatusPagamento,
    boleto,
    erro: null,
  })),

  on(PagamentoActions.buscarBoletoFalha, (state, { erro }) => ({
    ...state,
    status: 'ERRO' as StatusPagamento,
    boleto: null,
    erro,
  })),

  on(PagamentoActions.confirmarPagamento, state => ({
    ...state,
    status: 'PROCESSANDO' as StatusPagamento,
    erro: null,
  })),

  on(PagamentoActions.pagamentoSucesso, (state, { comprovante }) => ({
    ...state,
    status: 'APROVADO' as StatusPagamento,
    comprovante,
    erro: null,
  })),

  on(PagamentoActions.pagamentoFalha, (state, { erro }) => ({
    ...state,
    status: 'ERRO' as StatusPagamento,
    erro,
  })),

  on(PagamentoActions.resetarPagamento, () => estadoInicial),
);
