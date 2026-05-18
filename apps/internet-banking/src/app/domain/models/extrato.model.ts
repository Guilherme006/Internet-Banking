export type TipoTransacao = 'CREDITO' | 'DEBITO';

export interface Transacao {
  readonly id: string;
  readonly tipo: TipoTransacao;
  readonly descricao: string;
  readonly valor: number;
  readonly dataHora: string;
  readonly saldoApos: number;
  readonly categoria?: string;
}

export interface Paginacao {
  readonly paginaAtual: number;
  readonly totalElementos: number;
  readonly tamanhoPagina: number;
  readonly totalPaginas: number;
}

export interface FiltroExtrato {
  readonly dataInicio: Date | null;
  readonly dataFim: Date | null;
  readonly tipo: TipoTransacao | 'TODOS';
  readonly pagina: number;
  readonly tamanhoPagina: number;
}

export interface ResumoExtrato {
  readonly totalCredito: number;
  readonly totalDebito: number;
  readonly saldoAtual: number;
}

export const filtroExtratoInicial: FiltroExtrato = {
  dataInicio: null,
  dataFim: null,
  tipo: 'TODOS',
  pagina: 0,
  tamanhoPagina: 10,
};
