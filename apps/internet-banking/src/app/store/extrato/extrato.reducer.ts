import { createReducer, on } from '@ngrx/store';
import { FiltroExtrato, Paginacao, Transacao, filtroExtratoInicial } from '../../domain/models/extrato.model';
import { ExtratoActions } from './extrato.actions';

export interface ExtratoState {
  readonly transacoes: Transacao[];
  readonly carregando: boolean;
  readonly erro: string | null;
  readonly paginacao: Paginacao;
  readonly filtros: FiltroExtrato;
}

const paginacaoInicial: Paginacao = {
  paginaAtual: 0,
  totalElementos: 0,
  tamanhoPagina: 10,
  totalPaginas: 0,
};

const estadoInicial: ExtratoState = {
  transacoes: [],
  carregando: false,
  erro: null,
  paginacao: paginacaoInicial,
  filtros: filtroExtratoInicial,
};

export const extratoReducer = createReducer(
  estadoInicial,

  on(ExtratoActions.carregarExtrato, (state, { filtro }) => ({
    ...state,
    carregando: true,
    erro: null,
    filtros: filtro,
  })),

  on(ExtratoActions.carregarExtratoSucesso, (state, { transacoes, paginacao }) => ({
    ...state,
    carregando: false,
    transacoes,
    paginacao,
    erro: null,
  })),

  on(ExtratoActions.carregarExtratoFalha, (state, { erro }) => ({
    ...state,
    carregando: false,
    erro,
    transacoes: [],
  })),

  on(ExtratoActions.atualizarFiltros, (state, { filtro }) => ({
    ...state,
    filtros: filtro,
  })),

  on(ExtratoActions.resetarEstado, () => estadoInicial),
);
